var Recognition = {

    createModel: function createModelFn() {

        this.location = new AR.RelativeLocation(null, -5, -5, 0);
        this.location2 = new AR.GeoLocation(60.221265, 24.8050491, 320.);
        this.modelLautasari = new AR.Model("/sdcard/3dModels/LauttasaariWaterTower.wt3", {
            onLoaded: Recognition.loadingStep,
            onClick: Recognition.toggleAnimateModel,
            scale: {
                x: 0.001,
                y: 0.001,
                z: 0.001
            },
            translate: {
                x: 0.0,
                y: 0.05,
                z: 0.0
            },
            rotate: {
                //heading: 90,
                //tilt: 90,
                //roll: 90
            }
        });


        this.indicatorImage = new AR.ImageResource("assets/indi.png");
        this.indicatorDrawable = new AR.ImageDrawable(this.indicatorImage, 0.1, {
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
        });
        Recognition.rotationAnimation = new AR.PropertyAnimation(Recognition.modelLautasari, "rotate.heading", -25, 335, 10000);
        this.obj = new AR.GeoObject(Recognition.location, {
            drawables: {
               cam: [Recognition.modelLautasari],
               indicator: [Recognition.indicatorDrawable]
            }
        });
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

}
$(document).ready(function(){
    Recognition.createModel();
});