const midi = {
    uploadInit() {
        $("#btn-upload .spinner").hide()

        const tbody = $("#table-info-tbody")

        // 유효 미디 파일
        let validFiles = []

        function resetAll() {
            $("#field-file").get(0).value = ""
            validFiles = []
            $("#table-info-tbody tr:not(#info-tr-proto)").remove()
        }

        $("#field-file").on("change", e => {
            if ($("#field-file").get(0).length >= 1) {
                resetAll()
            } else {
                $("#table-info-tbody tr:not(#info-tr-proto)").remove()
            }
            const evTarget = e.target
            console.log("before", evTarget.files)

            const errMsgs = []

            Array.from(evTarget.files).forEach(file => {

                const originalFileExt = file.name.substr(file.name.lastIndexOf(".") + 1).toLocaleLowerCase()

                if (file.type === "audio/midi" || originalFileExt === "mid" || originalFileExt === "rmi") {

                    if (file.type !== "audio/midi") {
                        errMsgs.push(file.name + "\n  - 파일 정보가 부족하므로 변환이 되지 않을 수 있습니다.")
                    }

                    validFiles.push(file)

                    const clone = $("#info-tr-proto").clone()

                    clone.find(".file-name").text(file.name)
                    clone.find(".category input").val("Uploaded")

                    const fileNameWithoutExt = file.name.substring(0, file.name.lastIndexOf("."))
                    clone.find(".custom-title input").val(fileNameWithoutExt)
                    clone.find(".file-size").text(Math.ceil(file.size / 1000) + " KB")
                    clone.show()
                    clone.attr("id", "")


                    tbody.append(clone)
                } else {
                    errMsgs.push(file.name + "\n  - 미디 파일이 아니거나 파일이 손상되었습니다.")
                }
            })

            if (errMsgs.length > 0) {
                alert(errMsgs.join("\n"))
            }

            console.log("after", validFiles)
        })

        $("#btn-change-category").on("click", e => {
            const catStr = $("#field-custom-category").val()
            tbody.find(".category input").val(catStr !== "" ? catStr : "Uploaded")
        })

        $("#btn-upload").on("click", e => {
            const files = validFiles

            if (files.length == 0) {
                alert("업로드할 파일을 선택하세요.")
                return false
            }

            const categories = $("#table-info-tbody tr:not(#info-tr-proto) .category input").map((i, el) => $(el).val())
            const titles = $("#table-info-tbody tr:not(#info-tr-proto) .custom-title input").map((i, el) => $(el).val())
            console.log(categories, titles)

            const formData = new FormData()

            files.forEach((file, i) => {
                formData.append("files", file)
                formData.append("categories", categories[i])
                formData.append("titles", titles[i])
            })

            const entries = formData.entries()

            $("#btn-upload .spinner").show()
            $("#btn-upload .message").text("작업을 처리하고 있습니다.....")
            fetch("/api/v1/midi/", {
                    method: "POST",
                    body: formData
                })
                .then(res => res.json())
                .then(data => {
                    console.log(data)

                    if (data.status == "NotAllowManyFile") {
                        alert("일반 회원은 최대 5개의 파일만 업로드할 수 있습니다.")
                        resetAll()
                        return
                    }

                    $(".modal").find(".upload-result").text(data.status)
                    $(".modal").find(".success-count").text(data.successList.length)
                    $(".modal").find(".failed-count").text(data.failedList.length)
                    $(".modal").modal("show")

                })
                .catch(err => {
                    console.error(err)

                    $(".modal").find(".upload-result").text("파일 전송에 실패했습니다.")
                    $(".modal").modal("show")

                    resetAll()
                })
                .finally(() => {
                    $("#btn-upload .spinner").hide()
                    $("#btn-upload .message").text("모든 파일 업로드")

                })
        })

        $("#btn-reset").on("click", e => {
            resetAll()
        })
    },
    playerInit() {
        Number.prototype.toMS = function(isMillisecond) {
            if (isMillisecond) {
                var num = parseInt(this) / 1000
            } else {
                var num = parseInt(this)
            }

            // console.log(num)
            var min = Math.floor(num / 60)
            var sec = Math.floor(num - (min * 60))
            if (min < 10) {
                min = "0" + min;
            }
            if (sec < 10) {
                sec = "0" + sec;
            }
            return min + ":" + sec
        }
        
        const example = [{
                "id": 1,
                "userId": 1,
                "category": "Uploaded",
                "customTitle": "amazonia",
                "originalFileName": "amazonia.mid"
            },
            {
                "id": 2,
                "userId": 1,
                "category": "Uploaded",
                "customTitle": "ample",
                "originalFileName": "ample.mid"
            },
            {
                "id": 3,
                "userId": 1,
                "category": "Uploaded",
                "customTitle": "arsenal-mixed-4bell-mid0",
                "originalFileName": "arsenal-mixed-4bell-mid0.mid"
            }
        ]

        // 현재 재생중인 곡 정보를 담는 객체
        const currentPlay = {
            trEl: null,
            fullEl: []
        }

        // ajax로 곡 목록을 가져와 새로운 $tr을 테이블에 append
        fetch("/api/v1/midi", {
                method: "GET"
            })
            .then(res => res.json())
            .then(data => {
                data.forEach((song, i) => {
                    const $tr = document.createElement("tr")
                    $tr.setAttribute("title", song.originalFileName + ` | 업로드 일자: [${song.createdDate}]`)
                    $tr.setAttribute("data-id", song.id)
                    $tr.innerHTML = `<th scope="row">${song.id}</th>
                    <td class="song-title"><span class="text-muted">[${song.category}]</span> ${song.customTitle}</td>
                    <td class="text-center"><span class="badge badge-info">${song.userId}</span></td>`
                    document.getElementById("table-info-tbody").appendChild($tr)
                    currentPlay.fullEl.push($tr)

                })
            })

        // 아이디를 정보로 받아 오디오를 재생하는 함수
        function loadAudio(audioCtx, id) {

            audioCtx.loop = false
            audioCtx.src = "/api/v1/midi/mp3/" + id
            audioCtx.load()
            audioCtx.play()
            

        }

        // 제목을 클릭하면 노래가 재생
        document.addEventListener("click", e => {
            if (e.target && e.target.classList.contains("song-title")) {
                const audio = document.getElementById("audio-player")
                const parentEl = e.target.parentElement
                loadAudio(audio, parentEl.dataset.id)
                currentPlay.trEl = parentEl // 현재 재생중인 곡의 tr을 currentPlay.trEl에 저장
                // audio.src = "http://cld3097web.audiovideoweb.com/va90web25003/companions/Foundations%20of%20Rock/13.01.mp3"
                
                // 플레이어 정보 갱신
                document.getElementById("play-title").innerHTML = parentEl.getElementsByClassName("song-title")[0].innerHTML
            }
        })

        // 곡이 끝나면 (ended) tr.nextSibling으로 다음 곡을 찾아 재생
        document.getElementById("audio-player").addEventListener("ended", e => {
            const audio = e.target
            const nextEl = currentPlay.trEl.nextSibling || document.querySelector("#table-info tbody tr")
            loadAudio(audio, nextEl.dataset.id)
            currentPlay.trEl = nextEl // 현재 재생중인 곡의 tr을 currentPlay.trEl에 저장
            
            // 플레이어 정보 갱신
            document.getElementById("play-title").innerHTML = nextEl.getElementsByClassName("song-title")[0].innerHTML

        })

        // 내림차순
        document.querySelector("#table-info .head-title").addEventListener("click", e => {
            const $trArr = document.querySelectorAll("#table-info tbody tr")
            const $tbody = document.querySelector("#table-info tbody")
            $tbody.innerHTML = ""
            Array.from($trArr).reverse().forEach((el, i) => {
                $tbody.append(el)
            })
        })

        // 검색
        document.getElementById("btn-search").addEventListener("click", e => {
            const keyword = document.getElementById("search").value.toLowerCase()
            const $tbody = document.querySelector("#table-info tbody")

            $tbody.innerHTML = ""

            Array.from(currentPlay.fullEl).forEach((el, i) => {
                const title = el.querySelector(".song-title").textContent.toLowerCase()
                if(title.indexOf(keyword) != -1) {
                    $tbody.append(el)
                }
            })
        })

        // 검색 초기화
        document.getElementById("btn-search-reset").addEventListener("click", e => {
            const $tbody = document.querySelector("#table-info tbody")

            $tbody.innerHTML = ""

            Array.from(currentPlay.fullEl).forEach((el, i) => {
                $tbody.append(el)
            })
        })

        // 검색창 숨김
        document.getElementById("btn-toggle-search").addEventListener("click", e => {
            toggleSearch(localStorage.getItem("midi_search_status") != "open")
        });

        (() => {
            toggleSearch(localStorage.getItem("midi_search_status") == "open")
        })();

        function toggleSearch(isToShow) {
            const $divSearch = document.getElementById("div-search")
            const $button = document.getElementById("btn-toggle-search")

            if(isToShow) {
                $divSearch.style.display = 'flex'
                $button.textContent = $button.dataset.hideMsg
                localStorage.setItem("midi_search_status", "open")
            } else {
               $divSearch.style.display = 'none'
               $button.textContent = $button.dataset.showMsg
               localStorage.setItem("midi_search_status", "close")
            }
        }

    },


}
