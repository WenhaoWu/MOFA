var World = {
	loaded: false,

	init: function initFn() {
		this.createOverlays();
	},

	createOverlays: function createOverlaysFn() {

		this.tracker = new AR.ClientTracker("assets/tracker.wtc", {
			onLoaded: this.worldLoaded
		});

		var imgOne = new AR.ImageResource("assets/bingo.jpg");
		var overlayOne = new AR.ImageDrawable(imgOne, 1, {
			offsetX: 0,
			offsetY: 0
		});

        var pageOne = new AR.Trackable2DObject(this.tracker, "entrance_*", {
            drawables: {
                cam: overlayOne
            }
        });

		var imgTwo = new AR.ImageResource("assets/kitty123.jpg");
        var overlayTwo = new AR.ImageDrawable(imgTwo, 1, {
            offsetX: 0.12,
            offsetY: -0.01
        });

        var pageTwo = new AR.Trackable2DObject(this.tracker, "side*", {
            drawables: {
                cam: overlayTwo
            }
        });


		var imgThree = new AR.ImageResource("assets/squirell.jpg");
        var overlayThree = new AR.ImageDrawable(imgThree, 1, {
            offsetX: 0,
            offsetY: 0
        });

        var pageThree = new AR.Trackable2DObject(this.tracker, "gym_*", {
            drawables: {
                cam: overlayThree
            }
        });
	},

	worldLoaded: function worldLoadedFn() {
    		document.getElementById('loadingMessage').innerHTML = "<div>Scan Target</div>";
    		setTimeout(function() {
    			var e = document.getElementById('loadingMessage');
    			e.parentElement.removeChild(e);
    		}, 5000);
    	}
};

World.init();
