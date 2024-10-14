<template>

  <div ref="container" 
       style="width:100%; height: 800px; background-color: #000000;bottom: 0px;margin:0 auto;position: relative;">
	<div class="buttons-box" id="buttonsBox">
	  <div ref="progressContainer" @click="seek" class="progress-container">
      <div :style="{ width: progressBarWidth + '%' }" class="progress-bar"></div>
	  <div :style="{ left: progressBarWidth + '%' }" class="progress-marker"></div>
	  <span>{{ totalDuration>=3600?formattedTotalDuring:0 }} / {{ formattedCurrentTime}}</span>
	  </div>
	<button @click="backwardOneMinute">⏮</button>
		  <button v-if="!playing" @click="playBtnClick">▶️</button>
		  <button v-if="playing" @click="pause">⏸</button>
		  <button @click="forwardOneMinute">⏭</button>
		  </div>
  </div>
   

</template>

<script>
let jessibucaPlayer = {};
export default {
  name: 'jessibuca',
  data() {
    return {
      playing: false,
      isNotMute: false,
      quieting: false,
      fullscreen: false,
      loaded: false, // mute
	  currentTime: 0,
	   progressBarWidth: 0,
	   relativeTime: 0,
	   duration: 3600, // Duration in seconds (1 hour)
	  totalDuration: 0,
	  count:0,
	  timer:'',
	  interval: null,
      speed: 0,
      performance: "", // 工作情况
      kBps: 0,
      btnDom: null,
      videoInfo: null,
      volume: 1,
      rotate: 0,
      vod: true, // 点播
      forceNoOffscreen: false,
    };
  },
  props: ['videoUrl', 'error', 'hasAudio', 'height'],
  created() {
    let paramUrl = decodeURIComponent(this.$route.params.url)
    this.$nextTick(() => {
      console.log(2222)
      this.updatePlayerDomSize()
      window.onresize = this.updatePlayerDomSize
      if (typeof (this.videoUrl) == "undefined") {
        this.videoUrl = paramUrl;
      }
      this.btnDom = document.getElementById("buttonsBox");
    })
  },
  mounted() {
    // Update progress bar periodically
   
     // this.timer=setInterval(this.updateProgressBar, 5000);
		this.updateProgressBar();

	
  },
  watch: {
    videoUrl: {
      handler(val, _) {
        this.$nextTick(() => {
          this.play(val);
        })
      },
      immediate: true
    }
  },
  methods: {
	updateProgressBar() {
	console.log(1);
	
	if(jessibucaPlayer[this._uid]){
		this.currentTime=jessibucaPlayer[this._uid].currentTime;
		this.totalDuration=jessibucaPlayer[this._uid].duration;
        this.relativeTime = this.totalDuration-3600>0?this.currentTime +3600-this.totalDuration:this.currentTime;
        this.progressBarWidth = (this.relativeTime / this.duration) * 100;
	  console.log(this.currentTime);
	  console.log(this.totalDuration);
	  console.log(jessibucaPlayer[this._uid].currentTime);
	  console.log(jessibucaPlayer[this._uid].duration);
	  console.log(this.relativeTime);
	  }
    },
    updatePlayerDomSize() {
      let dom = this.$refs.container;
      let width = dom.parentNode.clientWidth
      let height = (9 / 16) * width
      console.log(height)

      console.log(dom.clientHeight)
      if (height > dom.clientHeight) {
        height = dom.clientHeight
        width = (16 / 9) * height
      }
      if (width > 0 && height > 0) {
        dom.style.width = width + 'px';
        dom.style.height = height + "px";
        console.log(width)
        console.log(height)
      }
    },
    create() {
      let options = {
        container: this.$refs.container,
        autoWasm: true,
        background: "",
        controlAutoHide: false,
        debug: false,
        decoder: "static/js/jessibuca/decoder.js",
        forceNoOffscreen: false,
        hasAudio: typeof (this.hasAudio) == "undefined" ? true : this.hasAudio,
        heartTimeout: 5,
        heartTimeoutReplay: true,
        heartTimeoutReplayTimes: 3,
        hiddenAutoPause: false,
        hotKey: true,
        isFlv: false,
        isFullResize: false,
        isNotMute: this.isNotMute,
        isResize: false,
        keepScreenOn: true,
        loadingText: "请稍等, 视频加载中......",
        loadingTimeout: 10,
        loadingTimeoutReplay: true,
        loadingTimeoutReplayTimes: 3,
        openWebglAlignment: false,
        operateBtns: {
          fullscreen: false,
          screenshot: false,
          play: false,
          audio: false,
          record: false
        },
        recordType: "mp4",
        rotate: 0,
        showBandwidth: false,
        supportDblclickFullscreen: false,
        timeout: 10,
        useMSE: true,
        useWCS: location.hostname === "localhost" || location.protocol === "https:",
        useWebFullScreen: true,
        videoBuffer: 0.1,
        wasmDecodeErrorReplay: true,
        wcsUseVideoRender: true
      };
      console.log("Jessibuca -> options: ", options);
      jessibucaPlayer[this._uid] = new window.Jessibuca({...options});

      let jessibuca = jessibucaPlayer[this._uid];
      let _this = this;
      jessibuca.on("pause", function () {
        _this.playing = false;
      });
      jessibuca.on("play", function () {
        _this.playing = true;
      });
	  jessibuca.on("timeupdate", () => {
        this.currentTime = jessibucaPlayer[this._uid].currentTime;
      });
      jessibuca.on('loadedmetadata', () => {
        this.totalDuration = jessibucaPlayer[this._uid].duration;
      });
	  
      jessibuca.on("fullscreen", function (msg) {
        _this.fullscreen = msg
      });
      jessibuca.on("mute", function (msg) {
        _this.isNotMute = !msg;
      });
      jessibuca.on("performance", function (performance) {
        let show = "卡顿";
        if (performance === 2) {
          show = "非常流畅";
        } else if (performance === 1) {
          show = "流畅";
        }
        _this.performance = show;
      });
      jessibuca.on('kBps', function (kBps) {
        _this.kBps = Math.round(kBps);
      });
      jessibuca.on("videoInfo", function (msg) {
        console.log("Jessibuca -> videoInfo: ", msg);
      });
      jessibuca.on("audioInfo", function (msg) {
        console.log("Jessibuca -> audioInfo: ", msg);
      });
      jessibuca.on("error", function (msg) {
        console.log("Jessibuca -> error: ", msg);
      });
      jessibuca.on("timeout", function (msg) {
        console.log("Jessibuca -> timeout: ", msg);
      });
      jessibuca.on("loadingTimeout", function (msg) {
        console.log("Jessibuca -> timeout: ", msg);
      });
      jessibuca.on("delayTimeout", function (msg) {
        console.log("Jessibuca -> timeout: ", msg);
      });
      jessibuca.on("playToRenderTimes", function (msg) {
        console.log("Jessibuca -> playToRenderTimes: ", msg);
      });
    },
    playBtnClick: function (event) {
      this.play(this.videoUrl)
    },
    play: function (url) {
      console.log("Jessibuca -> url: ", url);
      if (jessibucaPlayer[this._uid]) {
        this.destroy();
      }
      this.create();
	   jessibucaPlayer[this._uid].currentTime=3600;
		jessibucaPlayer[this._uid].duration=3600;
      jessibucaPlayer[this._uid].on("play", () => {
        this.playing = true;
        this.loaded = true;
        this.quieting = jessibuca.quieting;
      });
      if (jessibucaPlayer[this._uid].hasLoaded()) {
        jessibucaPlayer[this._uid].play(url);
      } else {
        jessibucaPlayer[this._uid].on("load", () => {
          jessibucaPlayer[this._uid].play(url);
        });
      }
    },
    pause: function () {
      if (jessibucaPlayer[this._uid]) {
        jessibucaPlayer[this._uid].pause();
      }
      this.playing = false;
      this.err = "";
      this.performance = "";
    },
    screenshot: function () {
      if (jessibucaPlayer[this._uid]) {
        jessibucaPlayer[this._uid].screenshot();
      }
    },
    mute: function () {
      if (jessibucaPlayer[this._uid]) {
        jessibucaPlayer[this._uid].mute();
      }
    },
    cancelMute: function () {
      if (jessibucaPlayer[this._uid]) {
        jessibucaPlayer[this._uid].cancelMute();
      }
    },
	 forwardOneMinute() {
     if (jessibucaPlayer[this._uid]) {
        jessibucaPlayer[this._uid].currentTime = Math.min(jessibucaPlayer[this._uid].currentTime + 60, jessibucaPlayer[this._uid].duration);
	   //this.currentTime=Math.min(this.currentTime + 60, this.totalDuration);
	   this.updateProgressBar();
      }
    },
    backwardOneMinute() {
      if (jessibucaPlayer[this._uid]) {
      jessibucaPlayer[this._uid].currentTime = Math.max(jessibucaPlayer[this._uid].currentTime - 60, jessibucaPlayer[this._uid].duration-3600,0);
	  //this.currentTime=Math.max(this.currentTime - 60, this.totalDuration-3600);
	  this.updateProgressBar();
      }
    },
   
   
    seek(event) {
	
      const containerWidth = this.$refs.progressContainer.offsetWidth;
      const clickX = event.offsetX;
      const newTime = (clickX / containerWidth) * this.duration;
       const targetTime = (jessibucaPlayer[this._uid].duration-3600>0?jessibucaPlayer[this._uid].duration-3600:0) + newTime;
	   console.log(targetTime);
	   //this.currentTime=Math.min(Math.max(targetTime, 0), this.totalDuration);
      jessibucaPlayer[this._uid].currentTime = Math.min(Math.max(targetTime, 0), jessibucaPlayer[this._uid].duration);
		console.log(jessibucaPlayer[this._uid].currentTime);
		this.updateProgressBar();
    },
    destroy: function () {
	if(this.timer){
	 clearInterval(this.timer);}
      if (jessibucaPlayer[this._uid]) {
        jessibucaPlayer[this._uid].destroy();
      }
      if (document.getElementById("buttonsBox") == null) {
        this.$refs.container.appendChild(this.btnDom)
      }
      jessibucaPlayer[this._uid] = null;
      this.playing = false;
      this.err = "";
      this.performance = "";

    },
    fullscreenSwich: function () {
      let isFull = this.isFullscreen()
      jessibucaPlayer[this._uid].setFullscreen(!isFull)
      this.fullscreen = !isFull;
    },
    isFullscreen: function () {
      return document.fullscreenElement ||
        document.msFullscreenElement ||
        document.mozFullScreenElement ||
        document.webkitFullscreenElement || false;
    }
  },
   computed: {
    formattedCurrentTime() {
      const hours = Math.floor(this.currentTime / 3600).toString().padStart(2, '0');
      const minutes = Math.floor((this.currentTime % 3600) / 60).toString().padStart(2, '0');
      const seconds = Math.floor(this.currentTime % 60).toString().padStart(2, '0');
      return `${hours}:${minutes}:${seconds}`;
    },
	formattedTotalDuring() {
       const hours = Math.floor((this.totalDuration-3600)/ 3600).toString().padStart(2, '0');
       const minutes = Math.floor(((this.totalDuration-3600) % 3600) / 60).toString().padStart(2, '0');
      const seconds = Math.floor((this.totalDuration-3600) % 60).toString().padStart(2, '0');
      return `${hours}:${minutes}:${seconds}`;
    }
  },
  destroyed() {
  if(this.timer){
	 clearInterval(this.timer);}
    if (jessibucaPlayer[this._uid]) {
      jessibucaPlayer[this._uid].destroy();
    }
    this.playing = false;
    this.loaded = false;
    this.performance = "";
  },
}
</script>
<style>
.progress-container {
  width: 100%;
  height: 10px;
  background-color: #ccc;
  position: relative;
  cursor: pointer;
}
.progress-bar {
  height: 100%;
  background-color: #76c7c0;
  width: 0%;
}
.progress-marker {
  position: absolute;
  top: 0px;
  width: 10px;
  height: 10px;
  background-color: #ff0000;
  border-radius: 50%;
  transform: translateX(-50%);
}
.buttons-box {
  width: 100%;
  height: 150px;
  background-color: rgba(43, 51, 63, 0.7);
  position: absolute;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  left: 0;
  bottom: 0;
  user-select: none;
  z-index: 10;
}


</style>


