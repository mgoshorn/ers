function worldClicked() {
    console.log("World clicked");
}

function containerClicked() {
    console.log("Container was clicked");
}

function respondToClick() {
    let clicked = 0;
    let myCount = function() {
        document.getElementById('clicker').innerText("Do NOT click me!");
        return ++clicked;
    }
    console.log(`You have clicked on me ${myCount()} times`);
}

square = {
    x: 0,
    y: 0,
    h: 25,
    w: 25,
    r: (Math.sqrt(this.h) + Math.sqrt(this.w)) /2,
    dx: 12,
    dy: 3,
    color: "#222",

    update: function() {
        let canvas = document.getElementById('myCanvas');
        
        this.x += this.dx;
        this.y += this.dy;
        this.radius = (Math.sqrt(this.h) + Math.sqrt(this.y)) /2;
        cxt = canvas.getContext('2d');
        if(this.y + this.height > cxt.canvas.clientHeight) {this.dy = -Math.abs(this.dy * 0.95);}
        if(this.x + this.width > cxt.canvas.clientWidth) {this.dx = -Math.abs(this.dx * 0.95);}
        if(this.x < 0) {this.dx = Math.abs(this.dx);}
        if(this.y < 0) {this.dy = Math.abs(this.dy);}
        this.dy += 0.08;
        //console.log(`context width: ${cxt.canvas.clientWidth}, context height: ${cxt.canvas.clientHeight}`)
        // if((this.x + this.width > cxt.width) || this.x < 0) this.dx = (-this.dx);
        // if((this.y + this.height > cxt.height) || this.y < 0) this.dy = (-this.dy);
        console.log(`x: ${this.x}, y: ${this.y}, dx: ${this.dx}, dy: ${this.dy}`)
        if(this.y > cxt.canvas.clientHeight - this.h) {
            this.y = cxt.canvas.clientHeight - this.h;
            this.y = this.dy * 0.95;
            this.dx = this.dx * 0.95;
        }
    },

    draw: function(canvas) {
        cxt = canvas.getContext('2d');
        cxt.clearRect(0, 0, canvas.width, canvas.height);
        cxt.fillStyle = this.color;
        // cxt.fillRect(this.x, this.y, this.width, this.height);
        let radius = (Math.sqrt(this.w*this.w) + Math.sqrt(this.h * this.h)) / 2;
        cxt.beginPath();
        cxt.arc(this.x+this.w/2,this.y+this.h/2,radius,0,Math.PI*2,false);
        cxt.closePath();
        cxt.fill();
    }
}

function drawSquare() {
    console.log("HI");
    setInterval(function() {
        console.log("Drawing...");
        let canvas = document.getElementById('myCanvas');
        square.draw(canvas);
        square.update(canvas);
    }, 3);
}