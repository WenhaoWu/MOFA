$(document).ready(function(){
    $("#carousel-generic").carousel("pause");
    $("#myBtn").click(function(){
        $("#carousel-generic").carousel("prev");
    });
    // Go to the next item
    $("#myBtn2").click(function(){
        $("#carousel-generic").carousel("next");
    });
    $("#next-button").click(function(){
        $("#carousel-generic").carousel("next");
    });
});