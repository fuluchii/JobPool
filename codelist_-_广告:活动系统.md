###CodeList - 广告/活动系统

LIonFreemarker.java

	public class LionFreemarkerResult extends StrutsResultSupport{

    @Autowired
    private CacheService cacheService;

    private boolean ifCacheNeed = false;

    private String cacheKey;


    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public void setIfCacheNeed(boolean cacheNeed) {
        ifCacheNeed = cacheNeed;
    }

    public boolean isIfCacheNeed() {
        return ifCacheNeed;
    }
//

    private static final Logger LOG = LoggerFactory.getLogger(FreemarkerResult.class);

    protected ActionInvocation invocation;
    protected Configuration configuration;
    protected ObjectWrapper wrapper;
    protected FreemarkerManager freemarkerManager;
    private Writer writer;
    private boolean writeIfCompleted = false;
    /*
     * Struts results are constructed for each result execution
     *
     * the current context is availible to subclasses via these protected fields
     */
    protected String location;
    private String pContentType = "text/html";
    private static final String PARENT_TEMPLATE_WRITER = FreemarkerResult.class.getName() +  ".parentWriter";

    public LionFreemarkerResult() {
        super();
    }

    public LionFreemarkerResult(String location) {
        super(location);
    }

    @Inject
    public void setFreemarkerManager(FreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }

    public void setContentType(String aContentType) {
        pContentType = aContentType;
    }

    /**
     * allow parameterization of the contentType
     * the default being text/html
     */
    public String getContentType() {
        return pContentType;
    }

    /**
     * Execute this result, using the specified template locationArg.
     * <p/>
     * The template locationArg has already been interoplated for any variable substitutions
     * <p/>
     * this method obtains the freemarker configuration and the object wrapper from the provided hooks.
     * It them implements the template processing workflow by calling the hooks for
     * preTemplateProcess and postTemplateProcess
     */
    public void doExecute(String locationArg, ActionInvocation invocation) throws IOException, TemplateException {
        this.location = locationArg;
        this.invocation = invocation;
        this.configuration = getConfiguration();
        this.wrapper = getObjectWrapper();

        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
        cacheKey = locationArg;

        boolean isCacheKeyExists = true;
        String cachedFreemarkerTemplate = null;
        Template template = null;
        if(isIfCacheNeed()){
            if(!StringUtils.isBlank(cacheKey)){
                isCacheKeyExists = false;
            }else{
                CacheKey templateKey = new CacheKey(cacheKey);
                cachedFreemarkerTemplate = cacheService.get(templateKey);
            }
        }
        if(cachedFreemarkerTemplate != null && StringUtils.isNotBlank(cachedFreemarkerTemplate)){
            template = new Template(getLocation(),new StringReader(cachedFreemarkerTemplate),freemarkerManager.getConfig(),"utf-8");
        }else{
            String fileFromLion = LionConfigUtils.getProperty(getLocation(),"");

            if(StringUtils.isNotBlank(fileFromLion)){
                template = new Template(getLocation(),new StringReader(fileFromLion),freemarkerManager.getConfig(),"utf-8");
            }
        }
        …
        }