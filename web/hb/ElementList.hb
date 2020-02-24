<div class="panel panel-default" id="ElementList">
    <table class="table">
        <tbody>
            {{#each mData}}
            <tr>
                <td>{{this.mId}}</td>
                <td>{{this.mContent}}</td>
                <td>{{this.mLikes}}</td>
                <td><button class="ElementList-likebtn" data-value="{{this.mId}}"><span class="glyphicon glyphicon-thumbs-up"></span></button></td>
                <td><button class="ElementList-dislbtn" data-value="{{this.mId}}"><span class="glyphicon glyphicon-thumbs-down"></span></button></td>
                <td>{{this.mDislikes}}</td>
                <td><button class="ElementList-editbtn" data-value="{{this.mId}}">Edit Message</button></td>
                <td><button class="ElementList-delbtn" data-value="{{this.mId}}">Delete Message</button></td>
            </tr>
            {{/each}}
        </tbody>
    </table>
</div>