const express = require("express");
const router = express.Router();
const fetch = require('node-fetch');
const cors = require('cors');
router.use(cors());


router.get('/',function(req,res){
  url = "https://content.guardianapis.com/politics?api-key=bd8e7945-6f90-43c0-8d91-da2a82667f02&show-blocks=all"
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