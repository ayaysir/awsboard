const main = {

    youtubeParser(url, ...groups) {
        const container = "[youtube::ID]"
        return (groups && groups[6].length == 11) ? container.replace("ID", groups[6]) : url;
    },

    youtubeWrapper(match, id) {
        const container = `
        <div class="video-container">
            <iframe width="560" height="315" src="https://www.youtube.com/embed/#ID#" title="YouTube video player"
            frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowfullscreen></iframe>
        </div>`
        return container.replace("#ID#", id)
    },

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

        // 글에서 링크 감지
        // + 유튜브인 경우 유튜브 iframe으로 대체
        if($("#div-content").length != 0) {
            const content = $("#div-content").text()

            // 유튜브 URL 찾는 패턴
            const youtubeUrl = /(http:|https:)?(\/\/)?(www\.)?(youtube.com|youtu.be)\/(watch|embed)?(\?v=|\/)?(\S+)?/g
            const expUrl = /(((http(s)?:\/\/)\S+(\.[^(\n|\t|\s,)]+)+)|((http(s)?:\/\/)?(([a-zA-z\-_]+[0-9]*)|([0-9]*[a-zA-z\-_]+)){2,}(\.[^(\n|\t|\s,)]+)+))+/gi
            const youtubeContainerRegex = /\[youtube::(.+)\]/g

            const wrappedContent = content.replace(youtubeUrl, self.youtubeParser)
                .replace(expUrl, "<a href='$&' target='_blank'>$&</a>").replace(youtubeContainerRegex, self.youtubeWrapper)

            $("#div-content").html(wrappedContent)
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
