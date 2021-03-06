const express = require("express");
const router = express.Router();
const fetch = require('node-fetch');
const cors = require('cors');
router.use(cors());

router.get('/',function(req,res){
  
  url = "https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=bd8e7945-6f90-43c0-8d91-da2a82667f02"
  fetch(url)
          .then((response) => {
              return response.json();
              })
              .then((data) => {
                  
                  res.send(data)
          });  
});

module.exports = router;

