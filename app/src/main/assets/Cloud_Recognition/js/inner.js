$(document).ready(function(){
    //$.getJSON('http://dev.mw.metropolia.fi/mofa/Wikitude_1/geoLocator/poi_detail.php?id=42', function(result){ console.log(result); });
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
    // when the carousel slides, load the ajax content
    /*$('#carousel-generic').on('slid', function (e) {

    	// get index of currently active item
    	var idx = $('#carousel-generic .item.active').index();
    	var url = $('.item.active').data('url');

    	// ajax load from data-url
      	$('.item').html("wait...");
    	$('.item').load(url,function(result){
    	    var image = new Image();
            image.src = 'data:image/png;base64,iVBORw0K...';
            document.body.appendChild(image);
    	    $('#carousel-generic').carousel(idx);
    	});

    });*/
});