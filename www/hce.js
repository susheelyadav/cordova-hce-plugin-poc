var exec = require('cordova/exec');

module.exports = {
  setMessage: function(message, success, error) {
    exec(success || function(){}, error || function(){}, 'HcePlugin', 'setMessage', [message]);
  }
};
