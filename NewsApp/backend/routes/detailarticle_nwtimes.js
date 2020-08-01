const express = require("express");
const router = express.Router();
const fetch = require('node-fetch');
const cors = require('cors');
router.use(cors());

router.get('/',function(req,res){
  articleid=req.query.id
  url = "https://api.nytimes.com/svc/search/v2/articlesearch.json?fq=web_url:(\""+articleid+"\")&api-key=dUNE2fAaJGHoX7seEuL4Ga1QohQZE8gQ"
  fetch(url)
          .then((response) => {
              return response.json();
              })
              .then((data) => {
                  console.log(data)
                  res.send(data)
          });  
});

module.exports = router;