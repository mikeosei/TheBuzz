/**
 * The ElementList Singleton provides a way of displaying all of the data 
 * stored on the server as an HTML table.
 */
class ElementList {
    /**
     * The name of the DOM entry associated with ElementList
     */
    private static readonly NAME = "ElementList";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
    * Initialize the ElementList singleton.  
    * This needs to be called from any public static method, to ensure that the 
    * Singleton is initialized before use.
    */
    private static init() {
        if (!ElementList.isInit) {
            ElementList.isInit = true;
        }
    }

    /**
    * refresh() is the public method for updating the ElementList
    */
    public static refresh() {
        // Make sure the singleton is initialized
        ElementList.init();
        // Issue a GET, and then pass the result to update()
        $.ajax({
            type: "GET",
            url: backendUrl + "/messages",
            dataType: "json",
            success: ElementList.update
        });
    }

    /**
    * update() is the private method used by refresh() to update the 
    * ElementList
    */
    private static update(data: any) {
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
    }

    /**
    * clickDelete is the code we run in response to a click of a delete button
    */
    private static clickDelete() {
        // for now, just print the ID that goes along with the data in the row
        // whose "delete" button was clicked
        let id = $(this).data("value");
        $.ajax({
            type: "DELETE",
            url: backendUrl + "/messages/" + id,
            dataType: "json",
            success: ElementList.refresh
        });
    }

    /**
     * clickLike is the code we run in response to a click of a like button
     */
    private static clickLike() {
        // as in clickDelete, we need the ID of the row
        let id = $(this).data("value");
        $.ajax({
            type: "PUT",
            url: backendUrl + "/messages/" + id + "/like",
            dataType: "json",
            success: ElementList.refresh
        })
    }

    /**
     * clickDislike is the code we run in response to a click of a dislike button
     */
    private static clickDislike() {
        // as in clickDelete, we need the ID of the row
        let id = $(this).data("value");
        $.ajax({
            type: "PUT",
            url: backendUrl + "/messages/" + id + "/dislike",
            dataType: "json",
            success: ElementList.refresh
        })
    }

    /**
     * clickEdit is the code we run in response to a click of an edit message button
     */
    private static clickEdit() {
        // as in clickDelete, we need the ID of the row
        let id = $(this).data("value");
        EditEntryForm.show(id);
    }

}