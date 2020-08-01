const express = require("express");
const router = express.Router();
const fetch = require('node-fetch');
const cors = require('cors');
router.use(cors());


router.get('/',function(req,res){
  url = "https://api.nytimes.com/svc/topstories/v2/politics.json?api-key=dUNE2fAaJGHoX7seEuL4Ga1QohQZE8gQ"
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