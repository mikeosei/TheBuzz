(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['ElementList.hb'] = template({"1":function(container,depth0,helpers,partials,data) {
    var alias1=container.lambda, alias2=container.escapeExpression, lookupProperty = container.lookupProperty || function(parent, propertyName) {
        if (Object.prototype.hasOwnProperty.call(parent, propertyName)) {
          return parent[propertyName];
        }
        return undefined
    };

  return "            <tr>\n                <td>"
    + alias2(alias1((depth0 != null ? lookupProperty(depth0,"mId") : depth0), depth0))
    + "</td>\n                <td>"
    + alias2(alias1((depth0 != null ? lookupProperty(depth0,"mContent") : depth0), depth0))
    + "</td>\n                <td>"
    + alias2(alias1((depth0 != null ? lookupProperty(depth0,"mLikes") : depth0), depth0))
    + "</td>\n                <td><button class=\"ElementList-likebtn\" data-value=\""
    + alias2(alias1((depth0 != null ? lookupProperty(depth0,"mId") : depth0), depth0))
    + "\"><span class=\"glyphicon glyphicon-thumbs-up\"></span></button></td>\n                <td><button class=\"ElementList-dislbtn\" data-value=\""
    + alias2(alias1((depth0 != null ? lookupProperty(depth0,"mId") : depth0), depth0))
    + "\"><span class=\"glyphicon glyphicon-thumbs-down\"></span></button></td>\n                <td>"
    + alias2(alias1((depth0 != null ? lookupProperty(depth0,"mDislikes") : depth0), depth0))
    + "</td>\n                <td><button class=\"ElementList-editbtn\" data-value=\""
    + alias2(alias1((depth0 != null ? lookupProperty(depth0,"mId") : depth0), depth0))
    + "\">Edit Message</button></td>\n                <td><button class=\"ElementList-delbtn\" data-value=\""
    + alias2(alias1((depth0 != null ? lookupProperty(depth0,"mId") : depth0), depth0))
    + "\">Delete Message</button></td>\n            </tr>\n";
},"compiler":[8,">= 4.3.0"],"main":function(container,depth0,helpers,partials,data) {
    var stack1, lookupProperty = container.lookupProperty || function(parent, propertyName) {
        if (Object.prototype.hasOwnProperty.call(parent, propertyName)) {
          return parent[propertyName];
        }
        return undefined
    };

  return "<div class=\"panel panel-default\" id=\"ElementList\">\n    <table class=\"table\">\n        <tbody>\n"
    + ((stack1 = lookupProperty(helpers,"each").call(depth0 != null ? depth0 : (container.nullContext || {}),(depth0 != null ? lookupProperty(depth0,"mData") : depth0),{"name":"each","hash":{},"fn":container.program(1, data, 0),"inverse":container.noop,"data":data,"loc":{"start":{"line":4,"column":12},"end":{"line":15,"column":21}}})) != null ? stack1 : "")
    + "        </tbody>\n    </table>\n</div>";
},"useData":true});
})();
(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['EditEntryForm.hb'] = template({"compiler":[8,">= 4.3.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<div id=\"EditEntryForm\" class=\"modal fade\" role=\"dialog\">\n    <div class=\"modal-dialog\">\n        <div class=\"modal-content\">\n            <div class=\"modal-header\">\n                <h4 class=\"modal-title\">Edit Message</h4>\n            </div>\n            <div class=\"modal-body\">\n                <label for=\"EditEntryForm-message\">Message</label>\n                <textarea class=\"form-control\" id=\"EditEntryForm-message\"></textarea>\n            </div>\n            <div class=\"modal-footer\">\n                <button type=\"button\" class=\"btn btn-default\" id=\"EditEntryForm-OK\">OK</button>\n                <button type=\"button\" class=\"btn btn-default\" id=\"EditEntryForm-Close\">Cancel</button>\n            </div>\n        </div>\n    </div>\n</div>";
},"useData":true});
})();
(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['NewEntryForm.hb'] = template({"compiler":[8,">= 4.3.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<div id=\"NewEntryForm\" class=\"modal fade\" role=\"dialog\">\n    <div class=\"modal-dialog\">\n        <div class=\"modal-content\">\n            <div class=\"modal-header\">\n                <h4 class=\"modal-title\">Add a New Message</h4>\n            </div>\n            <div class=\"modal-body\">\n                <label for=\"NewEntryForm-message\">Message</label>\n                <textarea class=\"form-control\" id=\"NewEntryForm-message\"></textarea>\n            </div>\n            <div class=\"modal-footer\">\n                <button type=\"button\" class=\"btn btn-default\" id=\"NewEntryForm-OK\">OK</button>\n                <button type=\"button\" class=\"btn btn-default\" id=\"NewEntryForm-Close\">Cancel</button>\n            </div>\n        </div>\n    </div>\n</div>";
},"useData":true});
})();
(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['Navbar.hb'] = template({"compiler":[8,">= 4.3.0"],"main":function(container,depth0,helpers,partials,data) {
    return "<nav class=\"navbar navbar-default\">\n    <div class=\"container-fluid\">\n        <!-- Brand and toggle get grouped for better mobile display -->\n        <div class=\"navbar-header\">\n            <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" \n                data-target=\"#bs-example-navbar-collapse-1\" aria-expanded=\"false\">\n              <span class=\"sr-only\">Toggle navigation</span>\n              <span class=\"icon-bar\"></span>\n              <span class=\"icon-bar\"></span>\n              <span class=\"icon-bar\"></span>\n            </button>\n            <!-- Clicking the brand refreshes the page -->\n            <a class=\"navbar-brand\" href=\"/\">Lil Chengs</a>\n        </div>\n\n        <!-- Collect the nav links, forms, and other content for toggling -->\n        <div class=\"collapse navbar-collapse\" id=\"bs-example-navbar-collapse-1\">\n            <ul class=\"nav navbar-nav\">\n                <li>\n                    <a class=\"btn btn-link\" id=\"Navbar-add\">\n                        Add Message\n                        <span class=\"glyphicon glyphicon-plus\"></span><span class=\"sr-only\">Show Trending Posts</span>\n                    </a>\n                </li>\n            </ul>\n        </div>\n    </div>\n</nav>";
},"useData":true});
})();
