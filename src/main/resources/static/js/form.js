/*!
 *	表单插件-序列化及校验
 *
 *	Author: 任黎明
 *
!*/
;(function ($) {
	if (typeof jQuery === "undefined") {
		throw new Error("form.js requires jQuery");
	}
	
    $.fn.formJson = function () {
        var jsons = {};
        $(this.serializeArray()).each(function () {
            if (jsons[this.name]){
                if ($.trim(this.value)) {
                	jsons[this.name] = jsons[this.name] + "," + $.trim(this.value);
                }
            } else {
            	jsons[this.name] = $.trim(this.value);
            }
        });
        return jsons;
    };
    
    $.fn.valiform = function () {
		var isNext=true;
		$(this).find("input,select,textarea")
			.not("input[type='button']")
			.not("input[type='hidden']")
			.not("input[disabled='disabled']")
			.not("select[disabled='disabled']")
			.each(function() {
	    		isNext = new FormCheck(this).validate();
	    		return isNext;
	    	});
		return isNext;
	};
	
	function FormCheck(e){ this.element = $(e); }
	FormCheck.prototype = { 
		constructor: FormCheck, 
		options: { 
			index: 		0,
			tagName:	null,
			className:  "e-check",
			regex: 		null,
			message: 	null,
			width:		null,
			height:		35,
			posup:      true,
			top: 		null,
			left:		null,
			html:		null
		},
		value: function(){
			if (this.options.tagName === "select") {
				return this.element.find("option:selected").attr("value");
			} else {
				return this.element.val();
			}
		},
		validate: function() {
			this.options.tagName     = this.element.attr("tagName");
			this.options.regex 	     = eval(this.element.attr("data-vreg"));
			this.options.message     = this.element.attr("data-vmsg"); 
			this.options.value		 = this.value();
			
			if (this.options.message && !this.options.regex){				
				if (this.value()) {
					return true;
				} else {
					return this.tips(null, this.options.message);
				}
			}	
			
			if (!this.options.regex) {
				return true;	
			}
			if(this.options.regex.test(this.value())) {
				return true;
			}
			return this.tips(this.options.regex, this.options.message);
		},
		template: function(message){
			var _vleft 	  = this.element.attr("data-vleft");
			var _vwidth   = this.element.attr("data-vwidth");
			var _minwidth = this.element.outerWidth();
			
			this.options.posup = this.element.offset().top >= this.options.height;
			this.options.width = _vwidth?_vwidth:(_minwidth - 23);
			this.options.top   = this.element.position().top + (this.options.posup?0:(this.options.height + this.element.outerHeight()));
			this.options.left  = _vleft?_vleft:this.element.position().left;
			
			var style          = "min-width: "+_minwidth+"px;height: "+this.options.height+"px;line-height: 35px;font-size: 12px;color: #FF0000;margin: -35px 0 0 0!important;z-index: 1000;text-align: left!important;";
			this.options.html  = "<div class='"+this.options.className+"' data-index='"+(this.options.index++)+"' style='display: none;position: absolute;"+style+"'>";
			if(this.options.posup) {
				style 		   = "background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAjCAMAAAC5FDqkAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAACNUExURQAAAPmPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj/mPj//Hx+cAEvmPj/vN0feHi+cEFucKHPNgauskMvNeZv23uf21t/ulpecIGv21tecKGusiMv2pqf2zs/2np+smNOcMHPeFi/+7u+siMP23t/2vr/2rq4edo1oAAAATdFJOUwBKZuUY+dfnpSwGDGT1YDiVSMXscQCQAAAAoklEQVQoz82SyRLCIAxA41r3OkI7tHSgtG7o+P+/5whKI8u97wS8IQkhMMmKKJApEgUKMmohmp4xXgtf6Ds1VPpfaOrQWIjqc1SW5o5CoqaDoA0SHIseCYYFS4k2FYoHyb/g5CpVrn2gFdprSWfjdDps4pO1t7CJ4/rz5Ii6oZZm/5C/oXYcjZFT8NmczoRcVxAyn11ey21EQL7YryHK7jCs3z07OqQCSkHMAAAAAElFTkSuQmCC) no-repeat left 0;";
			} else {
				style		   = "background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAjCAMAAAC5FDqkAAAAAXNSR0IB2cksfwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAH5QTFRFAAAA+Y+P+Y+P+Y+P+Y+P+Y+P+Y+P+Y+P+Y+P/bOz/a+v+Y+P+Y+P+Y+P+Y+P/amp/8fH/aen+Y+P+Y+P+6Wl+Y+P+Y+P+Y+P+Y+P/7u7/bW1/bW3815m6yIw5wQW5wga6yY082Bq94WL5woa5wAS+83R5woc6yQy6yIy5wwc/T+1EwAAACp0Uk5TAGRgOPn1LBjl///XDAbF////pZX/Zkrn////////////////////////yvd/fgAAAKhJREFUeJzNktkWgiAQhmnfi4rSwFARwd7/BROxHFmu67vgnPm/wzZnEAJMpijIbL5YhvLVerPd7f38cDxhfL744kpwC7l96nuSWrocP0hfooTiICgN5/8iWPbkeVEyV4hKdlRiLIQJ69qsAgqqBqEoEKUEZEBoOeyQGggORR4TPHZU413eC3g5iz3XftAy+mBrXjZWTktMExvOtd/EeNt/IaIj+h1qhze5ND1q0JapiwAAAABJRU5ErkJggg==) no-repeat left 0;";
			}
			this.options.html += 	"<a href='javascript:;' style='padding-left: 0px;border: none!important;display: block;text-decoration: none;float: left;margin-left: 0px!important;"+style+"'>";
			style              = "float: right;min-width: "+this.options.width+"px;margin-left: 24px;color: #ff0000;font-size: 12px;background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAukAAAAjCAYAAAApKJTwAAAA40lEQVR42u3YwQnAIBBFwfR/8K592II9RZANIiEVBEOYB9PAnj57nCkFAACwV885jrtoLQAAgL1Grc9QdxAAAPiG+VE30gEAwEgHAACMdAAAMNIBAAAjHQAAjHQAAMBIBwAAIx0AADDSAQDASDfSAQDASAcAAIx0AAAw0gEAACMdAACMdAAAwEgHAAAjHQAAMNIBAMBIdwwAADDSAQAAIx0AAIx0AADASAcAACMdAAAw0gEAwEgHAADeN2qNnvMa6XOtAwAAe/VS1kCXJEmSJEmSJEmSJEmSJEmSJEmSJEnSf7oA0Uvg5iPtL+QAAAAASUVORK5CYII=) right 0;";
			if(!this.options.posup) {
				style	      += "margin-top: 5px;";
			}
			this.options.html += 		"<span style='"+style+"'>"+message+"</span>";
			this.options.html += 	"</a>";
			this.options.html += "</div>";
			return this.options.html;
		},
		tips: function(regex, message) {
			$("."+this.options.className).remove();
			this.element.before(this.template(message));
			
			this.element.prev().css("display", "block");
			this.element.prev().css("top", this.options.top);
			this.element.prev().css("left", this.options.left);
			
			var that = this;
			this.element.unbind();
			this.element.bind("change keyup input focus blur propertychange", 
					function() {
						if (regex){
							if (regex.test(that.value())) {
								that.element.parent().find("."+that.options.className).remove();
							}
						}else{
							if (that.value()) {
								that.element.parent().find("."+that.options.className).remove();
							}
						}
					});
			
			if(!this.element.attr("readonly")) {
				this.element.focus();
			}
			return false;
		}
	};
})(jQuery);