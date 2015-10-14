var Recognition = {
	tracker: null,
	//targetName : null,

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
        this.img = new AR.ImageResource("assets/bingo.jpg");
        this.imgOverlay = new AR.ImageDrawable(this.img, 0.7, {
            offsetX: 0,
            offsetY: 0,
        });
		//var imgUrl = "http://www.arkkitehtuurimuseo.fi/wikitude/img/Kallion_kirkko2.png";

        this.imgButton = new AR.ImageResource("assets/status.png");

        /*this.pageOneButton = this.createWwwButton("http://www.google.fi", 0.5, {
                            offsetX: 0,
                            offsetY: 0
                        });*/

        console.log("after button");
	},

	//Recognition of image on click
	onRecognition: function onRecognitionFn(recognized, response) {
		if (recognized) {
		    if(Recognition.pageOneButton == null){
                console.log("OMFG");
            }
            Recognition.pageOneButton = Recognition.createWwwButton(response.targetInfo.name, 0.5, {
                offsetX: 0,
                offsetY: 0
            });
			Recognition.wineLabelAugmentation = new AR.Trackable2DObject(Recognition.tracker, response.targetInfo.name , {
				drawables: {
					//cam: [Recognition.imgOverlay]
					cam: [Recognition.pageOneButton]
				}

			});
			console.log(response.targetInfo.name);
			//this.targetName = response.targetInfo.name;
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
	                        console.log("INSIDE FUNCTION");
                            options.onClick = function() {
                                //AR.context.openInBrowser(url);
                                document.location = "architectsdk://snapShotButton?name=" + url;

                            };
                            return new AR.ImageDrawable(this.imgButton, size, options);
                        },

	showUserInstructions: function showUserInstructionsFn() {
        $('#messageBox').text("Point camera to building of interest");
		setTimeout(function() {
			$("#messageBox").remove();
		}, 5000);
	}
};

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
Recognition.init();

/*function createButtonFn(url, size, options) {
                options.onClick = function() {
                    //AR.context.openInBrowser(url);
                    console.log("inside button function");
                    document.location = "architectsdk://craphost1";
                };
                return new AR.ImageDrawable(this.imgButton, size, options);
            }*/