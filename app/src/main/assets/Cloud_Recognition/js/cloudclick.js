var Recognition = {
	tracker: null,

	//Initialization
	init: function initFn() {
		this.createTracker();
		this.createOverlays();
	},

	//Crate tracker: 1st parameter cloud token, 2nd target archive token
	createTracker: function createTrackerFn() {
		Recognition.tracker = new AR.CloudTracker("13ac4bbc215277d1c50df0224f15335e", "55e6e40825e7dbe842761f01", {
			onLoaded: this.trackerLoaded,
			onError: this.trackerError
		});
	},

	//Error tracker
	trackerError: function trackerErrorFn(errorMessage) {
		alert(errorMessage);
	},

	//Overlay image, size and position
	createOverlays: function createOverlaysFn() {
        this.imgButton = new AR.ImageResource("assets/status.png");


        this.overlayPage = new AR.HtmlDrawable({
            uri: "assets/inner.html"
        }, 1, {
            viewportWidth: 1000,
            viewportHeight: 1000,
            offsetX: 0,
            offsetY: 0,
            clickThroughEnabled: true,
            //allowDocumentLocationChanges: false,
            onDocumentLocationChanged: function onDocumentLocationChangedFn(uri) {
                AR.context.openInBrowser(uri);
            }
        });

        var location = new AR.RelativeLocation(null, 0, -10, 0);
        var location2 = new AR.GeoLocation(60.2211255,  24.805152);
        var dist = location.distanceToUser();
        /*var lat = getUrlParameter('lat');
        console.log("LAT  " + lat);
        var lon = getUrlParameter('lon');
        console.log("LON" + lon);*/
        console.log("THIS IS SPARTAAAAAAAAAA!" + dist);
        //this.modelLautasari = new AR.Model("assets/halfLautasari.wt3", {
        this.modelLautasari = new AR.Model("/sdcard/3dModels/lautasari.wt3", {
            onLoaded: this.loadingStep,
            onClick: this.toggleAnimateModel,
            scale: {
                x: 50,
                y: 50,
                z: 50
            },
            translate: {
                x: 0.0,
                y: 0.05,
                z: 0.0
            },
            rotate: {
               // heading: 90,
               // tilt: 90,
                //roll: 90
            }
        });
        var indicatorImage = new AR.ImageResource("assets/indi.png");

                var indicatorDrawable = new AR.ImageDrawable(indicatorImage, 0.1, {
                    verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
                });
        Recognition.rotationAnimation = new AR.PropertyAnimation(this.modelLautasari, "rotate.tilt", -25, 335, 10000);
        var obj = new AR.GeoObject(location2, {
                            drawables: {
                               cam: [Recognition.modelLautasari],
                               indicator: [indicatorDrawable]
                            }
                        });
	},

	//Recognition of image on click
    onRecognition: function onRecognitionFn(recognized, response) {
        if (recognized) {
            var patt = /library_/;
            var pattKalion = /kalion_/;
            if(patt.test(response.targetInfo.name)){
                Recognition.lautasari = new AR.Trackable2DObject(Recognition.tracker, response.targetInfo.name, {
                    drawables: {
                        cam: [Recognition.modelLautasari]
                    }
                });
            } else if(pattKalion.test(response.targetInfo.name)) {
                Recognition.kirkko = new AR.Trackable2DObject(Recognition.tracker, response.targetInfo.name , {
                    drawables: {
                        cam: [Recognition.overlayPage]
                    }

                });
            } else {
                Recognition.pageOneButton = Recognition.createWwwButton(response.targetInfo.name, 0.5, {
                    offsetX: 0,
                    offsetY: 0
                });
                Recognition.wineLabelAugmentation = new AR.Trackable2DObject(Recognition.tracker, response.targetInfo.name , {
                    drawables: {
                        cam: [Recognition.pageOneButton]
                    }

                });
            }
        } else {
            $('#errorMessage').html("<div class='errorMessage'>Recognition failed! Try to stand in front of building</div>");
            setTimeout(function() {
                $('#errorMessage').empty();
            }, 5000);
        }
    },

	//Recognition error
	onRecognitionError: function onRecognitionError(errorCode, errorMessage) {
		alert("error code: " + errorCode + " error message: " + JSON.stringify(errorMessage));
	},

	//Scan button is pressed
	scan: function scanFn() {
		Recognition.tracker.recognize(this.onRecognition, this.onRecognitionError);
	},

	//Shows pop-up at the begining with instructions
	trackerLoaded: function trackerLoadedFn() {
		Recognition.showUserInstructions();
	},

	createWwwButton: function createWwwButtonFn(url, size, options) {
                            options.onClick = function() {
                                document.location = "architectsdk://snapShotButton?name=" + url;
                            };
                            return new AR.ImageDrawable(this.imgButton, size, options);
                        },

	showUserInstructions: function showUserInstructionsFn() {
        $('#messageBox').text("Point camera to building of interest");
		setTimeout(function() {
			$("#messageBox").remove();
		}, 5000);
	},

	toggleAnimateModel: function toggleAnimateModelFn() {
    		if (!Recognition.rotationAnimation.isRunning()) {
    			if (!Recognition.rotating) {
    				Recognition.rotationAnimation.start(-1);
    				Recognition.rotating = true;
    			} else {
    				Recognition.rotationAnimation.resume();
    			}
    		} else {
    			Recognition.rotationAnimation.pause();
    		}

    		return false;
    	}
};
$(document).ready(function(){

   /*var getUrlParameter = function getUrlParameter(sParam) {
       var sPageURL = decodeURIComponent(window.location.search.substring(1)),
           sURLVariables = sPageURL.split('&'),
           sParameterName,
           i;

       for (i = 0; i < sURLVariables.length; i++) {
           sParameterName = sURLVariables[i].split('=');

           if (sParameterName[0] === sParam) {
               return sParameterName[1] === undefined ? true : sParameterName[1];
           }
       }
   };*/
Recognition.init();
});


/*$(document).ready(function(){
    return $.ajax({
		type: "GET",
		url: "http://dev.mw.metropolia.fi/mofa/Wikitude_1/public/demo.php",
		success: function(data) {
		console.log(data);
		str = data.toString().replace(/\s/g, '');
		console.log(str);
			Recognition.init(str);
		}
	});
});*/