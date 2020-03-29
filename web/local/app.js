"use strict";
/**
 * EditEntryForm encapsulates all of the code for the form for editing an entry
 */
var EditEntryForm = /** @class */ (function () {
    function EditEntryForm() {
    }
    /**
     * Initialize the EditEntryForm by creating its element in the DOM and
     * configuring its buttons.  This needs to be called from any public static
     * method, to ensure that the Singleton is initialized before use
     */
    EditEntryForm.init = function () {
        if (!EditEntryForm.isInit) {
            $("body").append(Handlebars.templates[EditEntryForm.NAME + ".hb"]());
            $("#" + EditEntryForm.NAME + "-OK").click(EditEntryForm.submitForm);
            $("#" + EditEntryForm.NAME + "-Close").click(EditEntryForm.hide);
            EditEntryForm.isInit = true;
        }
    };
    /**
     * Refresh() doesn't really have much meaning, but just like in Navbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    EditEntryForm.refresh = function () {
        EditEntryForm.init();
    };
    /**
     * Hide the EditEntryForm.  Be sure to clear its fields first
     */
    EditEntryForm.hide = function () {
        $("#" + EditEntryForm.NAME + "-message").val("");
        $("#" + EditEntryForm.NAME).modal("hide");
    };
    /**
     * Show the EditEntryForm.  Be sure to clear its fields, because there are
     * ways of making a Bootstrap modal disapper without clicking Close, and
     * we haven't set up the hooks to clear the fields on the events associated
     * with those ways of making the modal disappear. Also set the
     * static id variable to what was passed in from the Edit Message button click
     *
     * @param ID The id of the message
     */
    EditEntryForm.show = function (ID) {
        EditEntryForm.messageID = ID;
        $("#" + EditEntryForm.NAME + "-message").val("");
        $("#" + EditEntryForm.NAME).modal("show");
    };
    /**
     * Send data to submit the form only if the field is valid.
     * Immediately hide the form when we send data, so that the user knows that
     * their click was received.
     */
    EditEntryForm.submitForm = function () {
        // get the value of message field, force it to be a string, and check 
        // that it is not empty
        var msg = "" + $("#" + EditEntryForm.NAME + "-message").val();
        if (msg === "") {
            window.alert("Please enter message");
            return;
        }
        EditEntryForm.hide();
        // set up an AJAX post.  When the server replies, the result will go to
        // onSubmitResponse
        $.ajax({
            type: "PUT",
            url: backendUrl + "/messages/" + EditEntryForm.messageID,
            dataType: "json",
            data: JSON.stringify({ mMessage: msg }),
            success: EditEntryForm.onSubmitResponse
        });
    };
    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a
     * result.
     *
     * @param data The object returned by the server
     */
    EditEntryForm.onSubmitResponse = function (data) {
        // If we get an "ok" message, clear the form and refresh the main 
        // listing of messages
        if (data.mStatus === "ok") {
            ElementList.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    };
    /**
     * The name of the DOM entry associated with EditEntryForm
     */
    EditEntryForm.NAME = "EditEntryForm";
    /**
     * Track if the Singleton has been initialized
     */
    EditEntryForm.isInit = false;
    return EditEntryForm;
}()); // end class EditEntryForm
/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */
var NewEntryForm = /** @class */ (function () {
    function NewEntryForm() {
    }
    /**
     * Initialize the NewEntryForm by creating its element in the DOM and
     * configuring its buttons.  This needs to be called from any public static
     * method, to ensure that the Singleton is initialized before use
     */
    NewEntryForm.init = function () {
        if (!NewEntryForm.isInit) {
            $("body").append(Handlebars.templates[NewEntryForm.NAME + ".hb"]());
            $("#" + NewEntryForm.NAME + "-OK").click(NewEntryForm.submitForm);
            $("#" + NewEntryForm.NAME + "-Close").click(NewEntryForm.hide);
            NewEntryForm.isInit = true;
        }
    };
    /**
     * Refresh() doesn't really have much meaning, but just like in Navbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    NewEntryForm.refresh = function () {
        NewEntryForm.init();
    };
    /**
     * Hide the NewEntryForm.  Be sure to clear its fields first
     */
    NewEntryForm.hide = function () {
        $("#" + NewEntryForm.NAME + "-message").val("");
        $("#" + NewEntryForm.NAME).modal("hide");
    };
    /**
     * Show the NewEntryForm.  Be sure to clear its fields, because there are
     * ways of making a Bootstrap modal disapper without clicking Close, and
     * we haven't set up the hooks to clear the fields on the events associated
     * with those ways of making the modal disappear.
     */
    NewEntryForm.show = function () {
        $("#" + NewEntryForm.NAME + "-message").val("");
        $("#" + NewEntryForm.NAME).modal("show");
    };
    /**
     * Send data to submit the form only if the field is valid.
     * Immediately hide the form when we send data, so that the user knows that
     * their click was received.
     */
    NewEntryForm.submitForm = function () {
        // get the values of message field, force it to be a string, and check 
        // that it is not empty
        var msg = "" + $("#" + NewEntryForm.NAME + "-message").val();
        if (msg === "") {
            window.alert("Please enter message");
            return;
        }
        NewEntryForm.hide();
        // set up an AJAX post.  When the server replies, the result will go to
        // onSubmitResponse
        $.ajax({
            type: "POST",
            url: backendUrl + "/messages",
            dataType: "json",
            data: JSON.stringify({ mMessage: msg }),
            success: NewEntryForm.onSubmitResponse
        });
    };
    /**
     * onSubmitResponse runs when the AJAX call in submitForm() returns a
     * result.
     *
     * @param data The object returned by the server
     */
    NewEntryForm.onSubmitResponse = function (data) {
        // If we get an "ok" message, clear the form and refresh the main 
        // listing of messages
        if (data.mStatus === "ok") {
            ElementList.refresh();
        }
        // Handle explicit errors with a detailed popup message
        else if (data.mStatus === "error") {
            window.alert("The server replied with an error:\n" + data.mMessage);
        }
        // Handle other errors with a less-detailed popup message
        else {
            window.alert("Unspecified error");
        }
    };
    /**
     * The name of the DOM entry associated with NewEntryForm
     */
    NewEntryForm.NAME = "NewEntryForm";
    /**
     * Track if the Singleton has been initialized
     */
    NewEntryForm.isInit = false;
    return NewEntryForm;
}());
/**
 * The ElementList Singleton provides a way of displaying all of the data
 * stored on the server as an HTML table.
 */
var ElementList = /** @class */ (function () {
    function ElementList() {
    }
    /**
    * Initialize the ElementList singleton.
    * This needs to be called from any public static method, to ensure that the
    * Singleton is initialized before use.
    */
    ElementList.init = function () {
        if (!ElementList.isInit) {
            ElementList.isInit = true;
        }
    };
    /**
    * refresh() is the public method for updating the ElementList
    */
    ElementList.refresh = function () {
        // Make sure the singleton is initialized
        ElementList.init();
        // Issue a GET, and then pass the result to update()
        $.ajax({
            type: "GET",
            url: backendUrl + "/messages",
            dataType: "json",
            success: ElementList.update
        });
    };
    /**
    * update() is the private method used by refresh() to update the
    * ElementList
    */
    ElementList.update = function (data) {
        // Remove the table of data, if it exists
        $("#" + ElementList.NAME).remove();
        // Use a template to re-generate the table, and then insert it
        $("body").append(Handlebars.templates[ElementList.NAME + ".hb"](data));
        // Find all of the delete buttons, and set their behavior
        $("." + ElementList.NAME + "-delbtn").click(ElementList.clickDelete);
        // Find all of the edit buttons, and set their behavior
        $("." + ElementList.NAME + "-editbtn").click(ElementList.clickEdit);
        // Find all of the like buttons, and set their behavior
        $("." + ElementList.NAME + "-likebtn").click(ElementList.clickLike);
        // Find all of the dislike buttons, and set their behavior
        $("." + ElementList.NAME + "-dislbtn").click(ElementList.clickDislike);
    };
    /**
    * clickDelete is the code we run in response to a click of a delete button
    */
    ElementList.clickDelete = function () {
        // for now, just print the ID that goes along with the data in the row
        // whose "delete" button was clicked
        var id = $(this).data("value");
        $.ajax({
            type: "DELETE",
            url: backendUrl + "/messages/" + id,
            dataType: "json",
            success: ElementList.refresh
        });
    };
    /**
     * clickLike is the code we run in response to a click of a like button
     */
    ElementList.clickLike = function () {
        // as in clickDelete, we need the ID of the row
        var id = $(this).data("value");
        $.ajax({
            type: "PUT",
            url: backendUrl + "/messages/" + id + "/like",
            dataType: "json",
            success: ElementList.refresh
        });
    };
    /**
     * clickDislike is the code we run in response to a click of a dislike button
     */
    ElementList.clickDislike = function () {
        // as in clickDelete, we need the ID of the row
        var id = $(this).data("value");
        $.ajax({
            type: "PUT",
            url: backendUrl + "/messages/" + id + "/dislike",
            dataType: "json",
            success: ElementList.refresh
        });
    };
    /**
     * clickEdit is the code we run in response to a click of an edit message button
     */
    ElementList.clickEdit = function () {
        // as in clickDelete, we need the ID of the row
        var id = $(this).data("value");
        EditEntryForm.show(id);
    };
    /**
     * The name of the DOM entry associated with ElementList
     */
    ElementList.NAME = "ElementList";
    /**
     * Track if the Singleton has been initialized
     */
    ElementList.isInit = false;
    return ElementList;
}());
/**
 * The Navbar Singleton is the navigation bar at the top of the page.  Through
 * its HTML, it is designed so that clicking the "brand" part will refresh the
 * page.  Apart from that, it has an "add" button, which forwards to
 * NewEntryForm
 */
var Navbar = /** @class */ (function () {
    function Navbar() {
    }
    /**
     * Initialize the Navbar Singleton by creating its element in the DOM and
     * configuring its button.  This needs to be called from any public static
     * method, to ensure that the Singleton is initialized before use.
     */
    Navbar.init = function () {
        if (!Navbar.isInit) {
            $("body").prepend(Handlebars.templates[Navbar.NAME + ".hb"]());
            $("#" + Navbar.NAME + "-add").click(NewEntryForm.show);
            Navbar.isInit = true;
        }
    };
    /**
     * Refresh() doesn't really have much meaning for the navbar, but we'd
     * rather not have anyone call init(), so we'll have this as a stub that
     * can be called during front-end initialization to ensure the navbar
     * is configured.
     */
    Navbar.refresh = function () {
        Navbar.init();
    };
    /**
     * Track if the Singleton has been initialized
     */
    Navbar.isInit = false;
    /**
     * The name of the DOM entry associated with Navbar
     */
    Navbar.NAME = "Navbar";
    return Navbar;
}());
/// <reference path="ts/EditEntryForm.ts"/>
/// <reference path="ts/NewEntryForm.ts"/>
/// <reference path="ts/ElementList.ts"/>
/// <reference path="ts/Navbar.ts"/>
/// This constant indicates the path to our backend server
var backendUrl = "https://lilchengs.herokuapp.com";
// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $;
// Prevent compiler errors when using Handlebars
var Handlebars;
// Run some configuration code when the web page loads
$(document).ready(function () {
    Navbar.refresh();
    NewEntryForm.refresh();
    EditEntryForm.refresh();
    ElementList.refresh();
});
