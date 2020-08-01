const express = require("express");
const router = express.Router();
const fetch = require('node-fetch');
const cors = require('cors');
const googleTrends = require('google-trends-api');
router.use(cors());


router.get('/',function(req,res){
  var searchterm = req.query.searchword;
  googleTrends.interestOverTime({keyword: searchterm, startTime: new Date('2019-06-01')})
        .then(function(results){
            var dt=JSON.parse(results)
            var dtt=dt.default.timelineData

            var vals=[]

            for(var i=0;i<dtt.length;i++)
            {
                vals[i]=dtt[i].value[0]
            }
            
            res.send({vals})
        })
        .catch(function(err){
        console.error(err);
        });
    }
);

module.exports = router;