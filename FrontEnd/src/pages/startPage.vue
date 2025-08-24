<script setup>
import {ref} from 'vue';
import signIn from './signIn.vue'
import TheRegister from './TheRegister.vue';
const selectedSection = ref('');
function showSection(section){
    selectedSection.value = section;
    console.log("Đã chọn : " + selectedSection);
}
const isClicked = ref(false);
function showDropDown(){
    isClicked.value = !isClicked.value;
}

</script>
<template>
    <div class="container">
        <div class="header">
            <img src="https://cdn.dribbble.com/userupload/28456975/file/original-923692d84cf3a6905b017e91981ed0af.gif" alt="">
            <div class="header-right">
                <a href="" class="contact">Liên hệ</a>
                <a href="#" class="menu" @click.prevent="showDropDown">
                    <p class="line"></p>
                    <p class="line"></p>
                    <p class="line"></p>
                    <ul class="dropdown" v-show="isClicked">
                        <li @click.prevent="showSection('introduce')"><a href="">Giới thiệu</a></li>
                        <li @click.prevent="showSection('feature')"><a href="">Tính năng</a></li>
                    </ul>
                </a>
            </div>
        </div>
        <div class="content">
            <div class="main-content" :class="{'shrink' : selectedSection !== ''}">
                <div class="content-left">
                    <p class="text">ABYSS  – quản lý siêu thị mini thông minh! 🛒✨</p>
                    
                    <div class="login-register">
                        <router-link to="/signIn">
                            <button class="login">Đăng nhập</button>
                        </router-link>
                        <router-link to="/TheRegister">
                            <button class="register">Đăng ký</button>
                        </router-link>
                    </div>
                </div>
                <div class="content-right">
                    <img src="./image.jpg" alt="">
                </div>
            </div>
            <div class="introduce" v-if="selectedSection === 'introduce'"> 
                <p style = "font-size: 20px; font-weight: 600; padding-left: 50px;">🔹 Giới thiệu chung về hệ thống ABYSS</p>
                <p>Ngày nay, việc quản lý siêu thị mini và cửa hàng bán lẻ đòi hỏi sự chính xác, linh hoạt và hiệu quả cao. ABYSS ra đời như một giải pháp tối ưu giúp các chủ cửa hàng quản lý hoạt động kinh doanh một cách dễ dàng và khoa học.</p>
                <p>Hệ thống ABYSS không chỉ hỗ trợ kiểm soát hàng tồn kho, quản lý đơn hàng mà còn cung cấp các tính năng phân tích doanh thu, tối ưu quy trình bán hàng và tích hợp phương thức thanh toán hiện đại. Với giao diện trực quan, dễ sử dụng, ABYSS phù hợp với mọi đối tượng người dùng, từ những người mới bắt đầu kinh doanh đến các chủ cửa hàng lâu năm.</p>
            </div>
            <div class="feature" v-if="selectedSection === 'feature'">
                <p style = "font-size: 25px; font-weight: 600; padding-left: 50px;">🔹 Tính năng nổi bật của ABYSS:</p>
                <p>✔️ Quản lý hàng hóa theo danh mục, kiểm soát số lượng tồn kho chính xác</p>
                <p>✔️ Hỗ trợ lập hóa đơn, theo dõi doanh thu theo ngày, tuần, tháng</p>
                <p>✔️ Phân quyền nhân viên, theo dõi hiệu suất làm việc</p>
                <p>✔️ Báo cáo chi tiết giúp chủ cửa hàng đưa ra quyết định kinh doanh hiệu quả</p>
            </div>
        </div>
    </div>

</template>
<style scoped>
html, body {
    margin: 0;
    padding: 0;
    height: 100%;
}
.container{
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    background-color: #f5f5f5;
    height: 100%;
    width: 100%;
    font-family: 'Noto Sans', sans-serif;
}
.header{
    position:sticky;
    background-color: #388be3;
    height: 10vh;
    box-shadow:  0 3px 3px #a7a6a6;
    padding: 0 50px;
    display: flex;
    flex-direction: row;
    justify-content:space-between;
    align-items: center;
}
.header img{
    border-radius: 10px;
    height: 50px;
    width: 150px;
    object-fit:cover;
}
.header-right{
    display:flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 30px;
}
.contact{
    background-color: rgb(214, 8, 8);
    color:#f5f5f5;
    font-size: 15px;
    border: 10px solid rgb(214, 8, 8);
    border-radius: 10px;
    padding: 0 10px;
    text-decoration: none;
}
.menu{
    position:relative;
    height: 40px;
    width: 40px;
    border-radius: 50%;
    background-color: #f5f5f5;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 5px;
}
.line{
    background-color: #388be3;
    width: 60%;
    height: 2px;
    border-radius: 10%;
    margin: 0;
}
/* .dropdown {
    position: absolute;
    top: 30px;
    left: -64px;
    background: white;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    padding: 0;
    list-style: none;
    width: 150px;
    display: none;
} */
.dropdown {
    position: absolute;
    top: 30px;
    left: -64px;
    background: white;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    padding: 0;
    list-style: none;
    width: 150px;
    visibility: hidden; 
    opacity: 0;
    transition: opacity 0.3s ease-in-out;
}


.menu:focus-within .dropdown {
    visibility: visible;
    opacity: 1;
}
.dropdown::after{
    content:"";
    position:absolute;
    top:-10px;
    left: 50%;
    border-left: 10px solid transparent;
    border-right: 10px solid transparent;
    border-bottom: 10px solid white;
}

.dropdown li {
    padding: 10px;
    cursor: pointer;
    border-bottom: 1px solid #ddd;
}
.dropdown li a{
    text-decoration: none;
    font-size: 15px;
    white-space: nowrap;
    width:max-content;
    color: black;
    display: block;
}

.dropdown li:visited {
    background: #f5f5f5;
}

.menu:hover .dropdown {
    display: block;
}
.content{
    height: 90vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 30px;
    padding: 0 50px;
}
.main-content{
    height: 90vh;
    display: flex;
    flex-direction: row;
    justify-content:space-between;
    align-items: center;
    transition: height .5s ease-out;
}
.main-content .shrink{
    height: 55vh;
}
.content-left{
    width: 60%;
    display: flex;
    flex-direction: column;
    gap: 50px;
}
.text{
   font-size: 56px;
}
.login-register{
    display:flex;
    flex-direction: row;
    justify-content: space-evenly;
    align-items: center;
}
.login, .register{
    background-color: #388be3;
    color:#f5f5f5;
    font-size: 25px;
    border: 10px solid #388be3;
    border-radius: 8px;
    white-space: nowrap;
    padding: 0 15px;
    cursor: pointer;
}
.content-right{
    padding-right: 30px;
}
.introduce, .feature{
    height: 35vh;
    display:flex;
    justify-content: flex-start;
    flex-direction: column;
    background-color: #388be3;
    color: #f5f5f5;
    width: 100vw;
}
.introduce p, .feature p{
    padding: 0 50px;
    margin-top: 10px;
    font-size: 18px;
    padding-left: 70px;
}

</style>