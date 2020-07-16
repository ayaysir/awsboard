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
    },

    save() {
        const data = {
            title: $("#input-title").val(),
            author: $("#input-author").val(),
            content: $("#txt-content").val(),
            authorId: $("#user-id").val()
        }

        $.ajax({
            type: "POST",
            url: "/api/v1/posts",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(data)
        }).done(() => {
            alert("글이 등록되었습니다.")
            window.location.href = "/"
        }).fail(err => {
            alert(JSON.stringify(err))
        })

    },

    update() {
        const data = {
            title: $("#input-title").val(),
            content: $("#txt-content").val()
        }

        const id = $("#input-id").val()

        $.ajax({
            type: "PUT",
            url: "/api/v1/posts/" + id,
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(data)
        }).done(() => {
            alert("글이 수정되었습니다.")
            window.location.href = "/"
        }).fail(err => {
            alert(JSON.stringify(err))
            console.log(err)
        })
    },

    delete() {
        
        const id = $("#input-id").val()
        
        $.ajax({
            type: "DELETE",
            url: "/api/v1/posts/" + id,
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        }).done(res => {
            console.log(res)
            alert(`${res}번 글이 삭제되었습니다.`)
            window.location.href = "/"
        }).fail(err => {
            alert(JSON.stringify(err))
            console.log(err)
        })

    }
}

main.init()
