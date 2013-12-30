###Lookatme - codelist：

[主要代码链接](https://github.com/fuluchii/LookAtMe)

hubot-gtalk.coffee

	# Description:
	#   for lookat me
	#
	# Dependencies:
	#   None
	#
	# Configuration:
	#   None
	#
	# Commands:
	#   

	module.exports = (robot) ->
  	jid             = "fuluchii@gmail.com"
  	password        = "bokuwame"
  	server          = process.env.GTALK_SERVER   || "talk.google.com"
  	port            = process.env.GTALK_PORT     || 5222
	
  	tab = require('crontab')
  	setTimeout((robot)->
    robot.http('http://localhost:8083/madgirls.json')
      .query(username: "fuluchii")
      .get() (err, res, body) ->
        moods = JSON.parse(body)
        robot.send({user:{id:"chainyzhao@gmail.com"}},moods["mood"])
  	,3000
  	);
  
  	robot.respond /mymood/, (message)->   
    message.http('http://localhost:8083/getmood.json')
      .query(username: "fuluchii")
      .get() (err, res, body) ->
        moods = JSON.parse(body)
        robot.send({user:{id:"chainyzhao@gmail.com"}},moods["mood"])
  