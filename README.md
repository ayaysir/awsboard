# awsboard
http://yoonbumtae.com/?p=2689

#### **개요**

이 예제는 예전에 만들었던 [Spring Boot 예제: 초간단 게시판](http://yoonbumtae.com/?p=1853) 과 비슷한 예제인데, 이클립스 대신 <span style="text-decoration: underline;">인텔리제이 커뮤니티</span> 버전을 바탕으로 이전에 사용하지 않았던 <span style="text-decoration: underline;">JUnit 단위 테스트</span>, <span style="text-decoration: underline;">JPA</span>, <span style="text-decoration: underline;">Lombok</span> 등을 사용하였고, <span style="text-decoration: underline;">아마존 웹 서비스</span>와 <span style="text-decoration: underline;">Travis CI</span>라는 자동 배포 서비스를 이용해 <span style="text-decoration: underline;">외부 인터넷 상에서 접속할 수 있도록</span> 하였습니다. 과정의 대부분은 [스프링 부트와 AWS로 혼자 구현하는 웹 서비스](https://github.com/jojoldu/freelec-springboot2-webservice) (이동욱 저)를 참조하였고, 스프링, Gradle 버전은 책에서 설명한 버전보다 최신 버전을 적용하였습니다. <span style="text-decoration: underline;">OAuth 2.0</span>을 적용해 게시판은 <span style="text-decoration: underline;">구글 로그인</span>과 <span style="text-decoration: underline;">네이버 아이디로 로그인</span> API를 사용하며, 로그인한 사람만 이용할 수 있습니다. 게시판 특징은 다음과 같습니다.

*   로그인이 되어있지 않다면 게시판의 글 목록만 보여주고, 내용은 볼 수 없게 한다.
*   게시글의 수정, 삭제 기능은 해당 글을 작성한 사용자만 접근할 수 있도록 한다.

#### **제작 시기**

2020.7.11 ~ 2020.7.16  

#### **프로젝트 구조**

![](http://yoonbumtae.com/wp-content/uploads/2020/07/-2020-07-16-오후-6.26.16-e1594891697165.png) ![](http://yoonbumtae.com/wp-content/uploads/2020/07/스크린샷-2020-07-16-오후-6.27.11.png)  

#### **코드 저장소 (GitHub)**

[https://github.com/ayaysir/awsboard](https://github.com/ayaysir/awsboard)  

#### **접속 주소**

[http://awsboard.yoonbumtae.com:9090/](http://awsboard.yoonbumtae.com:9090/) 또는 [yoonbumtae.com/portfolio/awsboard](http://yoonbumtae.com/portfolio/awsboard)  

#### **동작 내용**

![](http://yoonbumtae.com/wp-content/uploads/2020/07/스크린샷-2020-07-16-오후-6.36.28.png) 메인 화면

![](http://yoonbumtae.com/wp-content/uploads/2020/07/스크린샷-2020-07-16-오후-6.39.10.png) 구글 계정으로 로그인한 경우

![](http://yoonbumtae.com/wp-content/uploads/2020/07/스크린샷-2020-07-16-오후-6.40.08.png) 글쓰기 화면

![](http://yoonbumtae.com/wp-content/uploads/2020/07/스크린샷-2020-07-16-오후-6.40.49.png) 새로운 글 목록 출력

![](http://yoonbumtae.com/wp-content/uploads/2020/07/스크린샷-2020-07-16-오후-6.41.28.png) 새로운 글 읽기 페이지, 내가 쓴 글인 경우 [수정], [삭제] 버튼 표시됨

![](http://yoonbumtae.com/wp-content/uploads/2020/07/스크린샷-2020-07-16-오후-6.42.41.png) 내가 쓴 글이 아닌 경우, 글 읽기만 가능하고 수정, 삭제 버튼은 활성화되지 않음

![](http://yoonbumtae.com/wp-content/uploads/2020/07/스크린샷-2020-07-16-오후-6.43.43.png) 네이버로 로그인한 경우
