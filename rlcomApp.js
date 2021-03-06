
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./routes')


var app = module.exports = express.createServer();


// Configuration

app.configure(function(){
  app.set('views', __dirname + '/views');
  app.set('view engine', 'jade');
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(require('stylus').middleware({ src: __dirname + '/public' }));
  //app.use(app.router);
  app.use(express.static(__dirname + '/public'));
});

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true })); 
});

app.configure('production', function(){
  app.use(express.errorHandler()); 
});

app.set('view options', { layout: false });

// Routes

app.get('/', function(req, res) {
		res.render('index', {
			title: 'Randy Lubin - Homepage',
			error: null
		});
});

app.get('/meeting',function(req, res) {
    res.render('meeting', {
      title: 'Randy Lubin - Meeting Room',
      error: null
    });
});

app.get('/beta', function(req, res) {
    res.render('beta', {
      title: 'Randy Lubin Beta Site',
      error: null
    });
});

app.get('/beta2', function(req, res) {
    res.render('beta2', {
      title: 'Randy Lubin Beta Site',
      error: null
    });
});

app.listen(7200);
console.log("Express server listening on port %d in %s mode", app.address().port, app.settings.env);
