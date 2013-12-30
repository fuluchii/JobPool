###Codelist - job迁移web

[主要代码链接](http://code.dianpingoa.com/apple/shop-otahotel-web/tree/master/src/main/java/com/dianping/otahotel/web/tasks/task)

-----------------

orderFactory.java
	
	public class OrderCallFactory implements FactoryBean {
    private int otaId;
    private OrderBlockingLinkedQueue queue;
    private Date startDate;
    private Date endDate;
    private Map<String, Object> params;

    

    @Override
    public Object getObject() throws Exception {
        if (otaId == HotelOTAEnum.Booking.value) {
            return new BookingOrderCall(otaId, queue, params);
        } else if (otaId == HotelOTAEnum.Elong.value) {
            return new ElongOrderCall(otaId, queue, params);
        } else if (otaId == HotelOTAEnum.ElongOversea.value) {
            return new ElongOverseaOrderCall(otaId, queue, params);
        }
        return null;
    }

    @Override
    public Class getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

	}

------------------

SocketWatcher:

	public class JobSocketWatcher {

    private  Logger logger = Logger.getLogger(this.getClass());
    private final JobSocketServer jobSocketServer = new JobSocketServer();

    public JobSocketWatcher(){
        ExecutorService watcherService = Executors.newSingleThreadExecutor();
        watcherService.execute(jobSocketServer);
    }

    class JobSocketServer implements Runnable
    {
        private final ExecutorService socketHandlerService = Executors.newFixedThreadPool(5);
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(1989);
                while (true){
                    Socket socket = serverSocket.accept();
                    socketHandlerService.submit(new JobWatcherSocketHandler(socket));
                }
            } catch (IOException e) {
                logger.error("Exception occurs when socket starts...");
            }
        }


    }

    class JobWatcherSocketHandler extends Observable implements Runnable {

        private Socket socket;
        private BufferedReader bufferedReader;

        private PrintStream print;

        public JobWatcherSocketHandler(Socket socket){
            try {
                this.socket = socket;
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                print = new PrintStream(socket.getOutputStream());
            } catch (IOException e) {
                logger.error("Exception occurs when read socket input.");
            }
            this.addObserver(JobObserver.getInstance());

        }

        @Override
        public void run() {
            String command = "";
            try {
                while (!(command = bufferedReader.readLine()).equals("exit")){
                    JobControlCommand jobControlCommand = parseCommand(command);
                    if(jobControlCommand != null){
                        this.setChanged();
                        this.notifyObservers(jobControlCommand);
                        print.println("Fire job:"+jobControlCommand.getJobName());
                    }else{
                        print.println("Not Valid Command:"+command);
                    }
                }
            } catch (IOException e) {
                logger.error("Exception", e);
            } finally {
                if(!socket.isClosed()){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        logger.error("Exception occurs when close socket.", e);
                    }
                }
            }
        }

        private JobControlCommand parseCommand(String command){
            String[] commandStrs = command.split(",");
            if(commandStrs.length != 5){
                return null;
            }
            if(!StringUtils.isAlpha(commandStrs[0])){
                return null;
            }
            if(!StringUtils.isNumeric(commandStrs[1]) || !StringUtils.isNumeric(commandStrs[2]) || !StringUtils.isNumeric(commandStrs[3]) || !StringUtils.isNumeric(commandStrs[4])){
                return null;
            }



            String jobName = commandStrs[0];
            int jobcmd = Integer.parseInt(commandStrs[1]);
            int begin = Integer.parseInt("-"+commandStrs[2]);
            int end = Integer.parseInt(commandStrs[3]);
            JobControlCommand jobControlCommand = new JobControlCommand();
            jobControlCommand.setJobName(jobName);
            jobControlCommand.setBegin(begin);
            jobControlCommand.setEnd(end);
            jobControlCommand.setOtaId(Integer.parseInt(commandStrs[4]));
            jobControlCommand.setJobCommand(JobCommandEnum.getJobCommandEnum(jobcmd));
            return jobControlCommand;
        }
    }
	}

