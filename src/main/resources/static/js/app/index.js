const main = {

    init() {
        const self = this
        $("#btn-save").on("click", e => {
            self.save()
        })

        $("#btn-update").on("click", e => {
            self.update()
        })

        $("#btn-delete").on("click", e => {
            self.delete()
        })

        if($("#div-content").length != 0) {
            const content = $("#div-content").text()
            const expUrl = /(((http(s)?:\/\/)\S+(\.[^(\n|\t|\s,)]+)+)|((http(s)?:\/\/)?(([a-zA-z\-_]+[0-9]*)|([0-9]*[a-zA-z\-_]+)){2,}(\.[^(\n|\t|\s,)]+)+))+/gi
            const changedContent = content.replace(expUrl, "<a href='$&' target='_blank'>$&</a>")
            $("#div-content").html(changedContent)
        }

    },

    save() {

        const writeTo = $("body").data("writeTo")

        const data = {
            title: $("#input-title").val(),
            author: $("#input-author").val(),
            content: $("#txt-content").val(),
            authorId: $("#user-id").val()
        }

        $.ajax({
            type: "POST",
            url: "/api/v1/" + writeTo,
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(data)
        }).done(() => {
            alert("글이 등록되었습니다.")
            window.location.href = "/" + (writeTo != "posts" ? writeTo : "")
        }).fail(err => {
            alert(JSON.stringify(err))
        })

    },

    update() {

        const writeTo = $("body").data("writeTo")

        const data = {
            title: $("#input-title").val(),
            content: $("#txt-content").val()
        }

        const id = $("#input-id").val()

        $.ajax({
            type: "PUT",
            url: "/api/v1/" + writeTo + "/" + id,
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(data)
        }).done(() => {
            alert("글이 수정되었습니다.")
            window.location.href = "/" + (writeTo != "posts" ? writeTo : "")
        }).fail(err => {
            alert(JSON.stringify(err))
            console.log(err)
        })
    },

    delete() {
        
        const id = $("#input-id").val()
        if(!confirm("정말 이 글을 삭제하시겠습니까?")) {
            return false
        }

        const writeTo = $("body").data("writeTo")
        
        $.ajax({
            type: "DELETE",
            url: "/api/v1/" + writeTo + "/" + id,
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        }).done(res => {
            console.log(res)
            alert(`${res}번 글이 삭제되었습니다.`)
            window.location.href = "/" + (writeTo != "posts" ? writeTo : "")
        }).fail(err => {
            alert(JSON.stringify(err))
            console.log(err)
        })

    }
}

main.init()
