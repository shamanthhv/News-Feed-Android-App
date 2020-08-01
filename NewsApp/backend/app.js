var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var apiRouter =require('./routes/api');
var apiRouter_nwtimes =require('./routes/api_nwtimes');
var worldRouter = require('./routes/world')
var worldnwtimesRouter = require('./routes/world_nwtimes')
var politicsRouter =require('./routes/politics')
var politicsnwtimesRouter = require('./routes/politics_nwtimes')
var businessRouter = require('./routes/business')
var businessnwtimesRouter =require('./routes/business_nwtimes')
var technologyRouter = require('./routes/technology')
var technologynwtimesRouter =require('./routes/technology_nwtimes')
var sportsRouter = require('./routes/sports')
var sportsnwtimesRouter = require('./routes/sports_nwtimes')
var searchRouter = require('./routes/search')
var searchnwtimesRouter = require('./routes/search_nwtimes')
var detailRouter =require('./routes/detailarticle')
var detailnwtimesRouter =require('./routes/detailarticle_nwtimes')
var scienceRouter = require('./routes/science')
var trendingRouter = require('./routes/trending')
var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/api',apiRouter)
app.use('/nwtimes',apiRouter_nwtimes)
app.use('/world',worldRouter)
app.use('/worldnwtimes',worldnwtimesRouter)
app.use('/politics',politicsRouter)
app.use('/politicsnwtimes',politicsnwtimesRouter)
app.use('/business',businessRouter)
app.use('/businessnwtimes',businessnwtimesRouter)
app.use('/technology',technologyRouter)
app.use('/technologynwtimes',technologynwtimesRouter)
app.use('/sports',sportsRouter)
app.use('/sportsnwtimes',sportsnwtimesRouter)
app.use('/search',searchRouter)
app.use('/searchnwtimes',searchnwtimesRouter)
app.use('/detail',detailRouter)
app.use('/detailnwtimes',detailnwtimesRouter)
app.use('/science',scienceRouter)
app.use('/trending',trendingRouter)
// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
