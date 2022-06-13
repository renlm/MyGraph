/**
 * 扩展数组方法
 */
(function(arr) {
	arr.prototype.contains = function(obj) {
		var i = this.length;
		while (i--) {
			if (this[i] === obj) {
		   		return true;
			}
		}
		return false;
	}
})(Array);
/***
 * 封装一些常用的方法
 */
(function($) {
	$.extend({
		/**
		 * 标记title
		 */
		spanWithTitleFormatter: function (value) {
			if (value === null || value === '') {
				return '<span style="color: gray;">-</span>';
			} else {
				return '<span title="' + value + '">' + value + '</span>';
			}
		},
		/**
		 * 文档授权角色，1：浏览者，2：编辑者，3：管理员
		 */
		docMemberRoleFormatter: function (value) {
			if (value === 1) {
				return '浏览者';
			} else if (value === 2) {
				return '编辑者';
			} else if (value === 3) {
				return '管理员';
			} else {
				return value;
			}
		},
		/**
		 * 文档项目-访问级别
		 */
		docVisitLevelFormatter: function (value) {
			if (value === 1) {
				return '<span style="text-decoration: underline;font-style: italic;" title=\'只有项目成员才可以访问\'>私有</span>';
			} else if (value === 2) {
				return '<span style="text-decoration: underline;font-style: italic;" title=\'可以由任何登录用户访问\'>公开</span>';
			} else {
				return value;
			}
		},
		/**
		 * 打开Markdown文档查看器
		 * @param params uuid 唯一标识
		 * @param params title 标题
		 * @param params openNew 是否打开新网页（默认否）
		 * @param params iconCls 导航栏图标
		 */
		openMarkdownViewer: function (uuid, title, openNew, iconCls) {
			if(openNew) {
				top.window.open(ctx + '/markdown/viewer?uuid=' + uuid + '&name=' + title);
			} else {
				try {
					top.MyUI.addIndexTab({
						id : "MarkdownViewer-" + uuid,
						title : title,
						href : '/markdown/viewer?uuid=' + uuid + '&name=' + title,
						iconCls : iconCls ? iconCls : 'fa fa-eye'
					});
				} catch(e) {
					top.window.open(ctx + '/markdown/viewer?uuid=' + uuid + '&name=' + title);
				}
			}
		},
		/**
		 * 打开Markdown文档编辑器
		 * @param params uuid 唯一标识
		 * @param params title 标题
		 */
		openMarkdownEditor: function (uuid, title) {
			layer.open({
				type: 2,
			  	title: title,
			  	shadeClose: true,
  				shade: 0.8,
			  	area: ['100%', '100%'],
			  	content: ctx + '/markdown/editor?uuid=' + uuid + '&name=' + title
			});
		},
		/***
         * 通过js触发打开一个错误消息列表
         * @param opts 需要覆盖的属性
         * @param errors 错误消息列表
         * @returns {*|jQuery|HTMLElement}
         */
        errorsDialog: function (opts, errors) {
            var myDialogId = opts.id || (new Date()).getTime();
			var myDialogIDatagridId = myDialogId + "Content";
			var width = opts.width ? opts.width : 850;
            var $myDialog = $("<form id='" + myDialogId + "' style='overflow-x: hidden' class='myui'></form>");
            var defaultOptions = {
                id: myDialogId,
                title: opts.title ? opts.title : "错误消息",
                closed: false,
    	        cache: false,
                width: width,
                height: opts.height ? opts.height : 495,
                content: "<table id='" + myDialogIDatagridId + "'></table>",
                buttons: [
                    {
                   		text: "关闭", 
						iconCls: "fa fa-close",
						handler: function () {
                            $myDialog.dialog("destroy");
                        }
                    }
                ],
				onOpen: function() {
					var messages = [];
					if (errors) {
						if(Array.isArray(errors)) {
							errors.forEach(function(item, index) {
	                   			messages.push({ index: index, message: item });
	                   		});
						} else {
							messages.push({ index: 0, message: errors });
						}
					}
					$("#" + myDialogIDatagridId).datagrid({
						border: false,
						fitColumns: true,
						pagination: false,
						columns: [[
							{	
								field: 'seq',
	                    		title: '序号',
	                    		formatter:function(value, index) {
									if (!value) {
										return $("#" + myDialogIDatagridId).datagrid('getRowIndex',index) + 1;
									}
	                    		}
							},
							{ field: "message", title: "消息内容", width: width - 80 }
						]],
						data: messages
					});
				},
				onClose: function () {
					$myDialog.dialog('destroy');
				}
            };
            $myDialog.myuiDialog($.extend(true, {}, defaultOptions, opts));
            return $myDialog;
        },
		/**
		 * 创建表格文件导出任务
		 * @param params actuator 任务执行器
		 * @param params fileName 文件名
		 * @param params formID 参数表单ID
		 */
		createExcelExportTask: function (actuator, fileName, formID) {
			var paramJson = $("#" + formID).formJson();
			parent.layer.prompt({
					title: "新建导出任务（" + fileName + "）",
					formType: 0, 
					value: fileName + layui.util.toDateString(new Date(), ".yyyy-MM-dd.HH.mm"),
					skin: "layui-layer-rim layui-layer-prompt custom-layui-btn-center" 
				}, function (taskName, index) {
					var _tempindex = parent.layer.load(2);
					$.post(ctx + "/sys/file/createExcelExportTask", 
						{ actuator: actuator, originalFilename: taskName, paramJson: JSON.stringify(paramJson) },
						function(data){
							parent.layer.close(_tempindex);
							if(data.statusCode == 200) {
								parent.layer.close(index);
								parent.layer.msg(data.message?data.message:"任务已添加", { icon: 1, time: 1500 });
							}else{
								parent.layer.msg(data.message?data.message:"出错了", { icon: 5, shift:6 });
							}
						});
				});
		},
		/**
		 * 打开图形
		 */
		openGraphEditorFormatter: function (value, row, index) {
			return '<a href=\'javascript:void(0);\' style=\'text-decoration:underline;color:blue;\' data-value="' + value + '" data-index="' + index + '" onclick="$.openGraphEditor(\'' + row.uuid + '\', \'' + row.name + '\')">' + row.name + '</a>';
		},
		/**
		 * 打开图形预览
		 * @param params uuid 唯一标识
		 * @param params title 标题
		 * @param params openNew 是否打开新网页（默认否）
		 * @param params iconCls 导航栏图标
		 */
		openGraphViewer: function (uuid, title, openNew, iconCls) {
			if(openNew) {
				top.window.open(ctx + '/graph/viewer?uuid=' + uuid);
			} else {
				try {
					top.MyUI.addIndexTab({
						id : "GraphViewer-" + uuid,
						title : title,
						href : '/graph/viewer?uuid=' + uuid,
						iconCls : iconCls ? iconCls : 'fa fa-eye'
					});
				} catch(e) {
					top.window.open(ctx + '/graph/viewer?uuid=' + uuid);
				}
			}
		},
		/**
		 * 打开图形编辑器
		 * @param params uuid 唯一标识
		 * @param params title 标题
		 * @param params openNew 是否打开新网页（默认否）
		 * @param params iconCls 导航栏图标
		 */
		openGraphEditor: function (uuid, title, openNew, iconCls) {
			if(openNew) {
				top.window.open(ctx + '/graph/editor?uuid=' + uuid);
			} else {
				try {
					top.MyUI.addIndexTab({
						id : "GraphEditor-" + uuid,
						title : title,
						href : '/graph/editor?uuid=' + uuid,
						iconCls : iconCls ? iconCls : 'fa fa-edit'
					});
				} catch(e) {
					top.window.open(ctx + '/graph/editor?uuid=' + uuid);
				}
			}
		},
		/**
		 * 表格分页参数处理
		 */
		pageOnBeforeLoad: function (param) {
			if (param.q) {
				param.keywords = param.q;
				delete param.q;
			}
			param.current = param.page;
			param.size = param.rows;
			delete param.page;
			delete param.rows;
		},
		/**
		 * 树图标样式
		 */
		treeIconStyler: function (value, row) {
		    if(value && row.iconClsColour) {
		        return 'color:' + row.iconClsColour + ';';
		    }
		},
		/**
		 * 树名称格式化
		 */
		treeTextFormatter: function (value, row) {
		    if(row.textColour) {
		        return '<font color=' + row.textColour + '>' + value + '</font>';
		    } else {
		        return value;
		    }
		},
		/**
		 * 组织机构类型格式化
		 */
		orgTypeFormatter: function (value) {
			if(value == 1) {
		  		return '公司';
			} else if(value == 2) {
		  		return '部门';
			} else if(value == 3) {
				return '岗位';
		 	} else {
				return value;
			}
		},
		/**
		 * 授权状态格式化
		 */
		accessAuthFormatter: function (value) {
			if(value) {
		        return '<span style=\'color:green\'>已授权</span>';
		    } else {
		        return '<span style=\'color:red\'>未授权</span>';
		    }
		},
		/**
		 * 资源类别格式化
		 */
		resourceTypeFormatter: function (value) {
			if(value == 'menu') {
		        return '菜单';
		    } else if(value == 'button') {
		        return '按钮';
		    } else if(value == 'urlInsidePage') {
		        return '链接-内页';
		    } else if(value == 'urlNewWindows') {
		        return '链接-新窗口';
		    } else if(value == 'more') {
		        return '更多功能';
		    } else if(value == 'permission') {
		        return '权限';
		    } else if(value == 'markdown') {
				return 'Markdown文档';
			} else {
		        return value;
		    }
		},
		/**
		 * 资源类别格式化（带按钮）
		 */
		resourceTypeBtnFormatter: function (value, row) {
			if(value == 'menu') {
		        return '菜单';
		    } else if(value == 'button') {
		        return '按钮';
		    } else if(value == 'urlInsidePage') {
		        return '链接-内页';
		    } else if(value == 'urlNewWindows') {
		        return '链接-新窗口';
		    } else if(value == 'more') {
		        return '更多功能';
		    } else if(value == 'permission') {
		        return '权限';
		    } else if(value == 'markdown') {
				return '<a href="javascript:$.openMarkdownEditor(\'' + row.resourceId + '\',\'' + row.text + '\');" class="l-btn myui-btn-green l-btn-small l-btn-plain" style="width: 100px;"><span class="l-btn-left l-btn-icon-left"><span class="l-btn-text" style="margin: 0px;">Markdown文档</span></span></a>';
			} else {
		        return value;
		    }
		},
		/**
		 * 禁用状态格式化
		 */
		disabledFormatter: function (value) {
			if(value) {
				return '<span style=\'color:red;\'>禁用</span>';
			} else {
				return '<span style=\'color:green;\'>启用</span>';
			}
		},
		/**
		 * 字节大小格式化
		 */
		formatBytes: function (value) {
			if(value === 0 || value === 1) {
				return value + " byte";
			} else if(value) {
				if(value >= 1024*1024*1024*1024) {
					return (Math.floor(value/1024/1024/1024/1024 * 100)/100) + " TiB"; 
				} else if(value >= 1024*1024*1024) {
					return (Math.floor(value/1024/1024/1024 * 100)/100) + " GiB"; 
				} else if(value >= 1024*1024) {
					return (Math.floor(value/1024/1024 * 100)/100) + " MiB"; 
				} else if(value >= 1024) {
					return (Math.floor(value/1024 * 100)/100) + " KiB"; 
				} else {
					return value + " bytes";
				}
			} else {
				return null;
			}
		},
		/**
		 * 序号格式化
		 */
		seqFormatter: function (value, row, index) {
			if(value) { } else if(row) { }
			return index + 1;
		},
		/**
		 * 是否状态格式化
		 */
		yesNoFormatter: function (value) {
			var yes = value ? true : false;
			if(value === null) {
				return '-';
			} else if(value === 'false') {
				yes = false;
			} else if(value === '0') {
				yes = false;
			} else if(value === 0) {
				yes = false;
			}
			if(yes) {
				return '是';
			} else {
				return '否';
			}
		},
		/**
		 * 否是状态格式化
		 */
		noYesFormatter: function (value) {
			if(value) {
				return '<span style=\'color:gray;\'>否</span>';
			} else {
				return '<span style=\'color:green;\'>是</span>';
			}
		},
		/**
		 * 创建时间格式化
		 */
		createdAtFormatter: function (value) {
			if(!value) {
				return null;
			}
			return layui.util.toDateString(value, "yyyy/MM/dd HH:mm:ss");
		},
		/**
		 * 更新时间格式化
		 */
		updatedAtFormatter: function (value) {
			if(!value) {
				return null;
			}
			return layui.util.toDateString(value, "yyyy/MM/dd HH:mm:ss");
		}
	});
})(jQuery);
/***
 * 汉化
 */
(function($) {
	if (!$.fn 
		|| !$.fn.combotreegrid
		|| !$.fn.combotreegrid.defaults) {
		return;
	}
	$.fn.combotreegrid.defaults.missingMessage = '必选';
})(jQuery);
/***
 * 表单验证器
 */
(function($) {
	if (!$.fn 
		|| !$.fn.validatebox 
		|| !$.fn.validatebox.defaults 
		|| !$.fn.validatebox.defaults.rules) {
		return;
	}
	$.extend($.fn.validatebox.defaults.rules, {
		username: {
	        validator: function(value) {
	            return new RegExp("^[A-Z-a-z_\\d]{3,20}$").test(value);
	        },
	        message: '账号限定3~20个字符，只能包含字母、数字、下划线或横线'
	    },
		password: {
	        validator: function(value) {
	            return new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W)[\\s\\S]{6,20}").test(value);
	        },
	        message: '密码限定6~20个字符，大小写字母、数字及特殊字符至少各一个'
	    },
		notStartWith: {
	        validator: function(value, param) {
	            return new RegExp("^(?!" + param[0] + ")").test(value);
	        },
	        message: '不能以{0}开头'
	    },
		decimal: {
	        validator: function(value, param) {
	            return new RegExp("^([1-9]+[\\d]{0," + (param[0] - param[1]) + "}(.[0-9]{1," + param[1] + "})?)$").test(value);
	        },
	        message: '输入内容只能是数字，最多{0}位，小数位不超过{1}位'
	    },
        equals: {
            validator: function (b, c) {
                return b == $(c[0]).val()
            }, message: "两次输入的内容不一致"
        }, cellphone: {
            validator: function (a) {
                return /^1(3|4|5|7|8|9)\d{9}$/.test(a)
            }, message: "请输入有效的手机号码"
        }, telephone: {
            validator: function (a) {
                return /(^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$)|(^((\d3)|(\d{3}\-))?(1[345789]\d{9})$)/.test(a)
            }, message: "请输入有效的电话号码"
        }, maxLength: {
            validator: function (a, b) {
                return a.length < b[0]
            }, message: "输入内容长度必须小于{0}"
        }, minLength: {
            validator: function (a, b) {
                return a.length > b[0]
            }, message: "输入内容长度必须大于{0}"
        }, idCard: {
            validator: function (a) {
                return /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/.test(a)
            }, message: "请输入正确的身份证号"
        }, postcode: {
            validator: function (a) {
                return /^[1-9]\d{5}(?!\d)$/.test(a)
            }, message: "请输入正确的邮政编码"
        }, date: {
            validator: function (a) {
                return /^[1-2][0-9][0-9][0-9]-[0-1]{0,1}[0-9]-[0-3]{0,1}[0-9]$/.test(a)
            }, message: "请输入正确的日期"
        }, alphaDash: {
            validator: function (a) {
                return /^[\w-]+$/.test(a)
            }, message: "输入内容只能是数字、字母、下划线或横线"
        }, alphaNum: {
            validator: function (a) {
                return /^[a-z0-9]+$/i.test(a)
            }, message: "输入内容只能是数字和字母"
        }, number: {
            validator: function (a) {
                return /^\d+$/.test(a)
            }, message: "输入内容只能是数字"
        }
    });
})(jQuery);
/***
 * 消息框重写
 */
(function($) {
	if (!$.messager) {
		return;
	}
	$.extend($.messager, {
   		myuiAlert:function(title,msg,icon,fn){
			var dlg = $.messager.alert(title,msg,icon,fn);
			dlg.next().find('a').addClass('myui-btn-red').find(".l-btn-left").addClass("l-btn-icon-left").append('<span class="l-btn-icon fa fa-check-circle">&nbsp;</span>').find('.l-btn-text').css('height', '28px').css('line-height', '28px').next().css('cssText', 'margin-top:-5px!important;');
			return dlg;
		},
		myuiConfirm:function(title,msg,fn){
			var dlg = $.messager.confirm(title,msg,fn);
			dlg.next().find('a:eq(0)').addClass('myui-btn-blue').find(".l-btn-left").addClass("l-btn-icon-left").append('<span class="l-btn-icon fa fa-check-circle">&nbsp;</span>').find('.l-btn-text').css('height', '28px').css('line-height', '28px').next().css('cssText', 'margin-top:-5px!important;');
			dlg.next().find('a:eq(1)').addClass('myui-btn-red').find(".l-btn-left").addClass("l-btn-icon-left").append('<span class="l-btn-icon fa fa-close">&nbsp;</span>').find('.l-btn-text').css('height', '28px').css('line-height', '28px').next().css('cssText', 'margin-top:-5px!important;');
			return dlg;
		}
    });
})(jQuery);
/***
 * 对话框重写
 */
(function($) {
	$.fn.myuiDialog = function (_2c1, _2c2) {
		var dlg = this.dialog(_2c1, _2c2);
		dlg.prev().find('.panel-title').after('<div class="panel-icon fa fa-windows"></div>').css('padding-left', '30px').next().css('margin-top', '-9px');
		dlg.next().find('.fa.fa-plus').parent().parent().addClass('myui-btn-normal myui-btn-blue');
		dlg.next().find('.fa.fa-minus').parent().parent().addClass('myui-btn-normal myui-btn-brown');
		dlg.next().find('.fa.fa-save').parent().parent().addClass('myui-btn-normal myui-btn-green');
		dlg.next().find('.fa.fa-mail-reply').parent().parent().addClass('myui-btn-normal myui-btn-black');
		dlg.next().find('.fa.fa-close').parent().parent().addClass('myui-btn-normal myui-btn-red');
		dlg.next().find('.left').parent().parent().css('float', 'left');
		return dlg;
	}
})(jQuery);
/***
 * 图标选择器
 */
(function(a) {
	function b(b) {
        var c = a(this).val(), d = b.data.target, f = a(d), h = f.combobox("options"), i = f.combobox("panel"),
            j = i[0].children[0].className;
        switch (f.combobox("showPanel"), b.keyCode) {
            case 38:
                break;
            case 40:
                break;
            case 37:
                break;
            case 39:
                break;
            case 13:
                return !1;
            case 9:
                return !1;
            case 27:
                break;
            default:
                h.editable && e(g, j, c)
        }
    }

    function c(b) {
        var c = a.data(b, "combobox"), e = c.options, g = e.id;
        a("#" + g).combobox("panel").addClass("icon-panel"), a('<div class="iconSelectPanel' + Math.ceil(100 * Math.random()) + '"><div style="padding:6px 10px;" class="iconlist"></div></div>').appendTo(".icon-panel"), a("#" + g).next("span").children(" .textbox-addon").addClass("icon-box" + Math.ceil(100 * Math.random()));
        var h = a(b).val();
        h && (d(), f(g, h));
        var i = "";
        setTimeout(function () {
            i = a("#" + g).combobox("getValue"), i && (d(), f(g, i))
        }, 500)
    }

    function d(b) {
        a.ajax({
            url: ctx + '/fontAwesome/getIcons', type: "get", dataType: "json", success: function (a) {
                g = a, b && e(g, b)
            }
        })
    }

    function e(b, c, d) {
        var e = [];
        d ? b.filter(function (a) {
            if (a.indexOf(d) >= 0) return e.push(a)
        }) : e = b, a("." + c + " .iconlist").empty();
        var f = "";
        a.each(e, function (a, b) {
            f += '<span class="fa ' + e[a] + '" title="' + b + '" style="width:30px;height:30px;border:1px solid #ccc;border-radius:8px;text-align:center;padding: 5px;font-size: 16px;margin: 4px 4px 4px 4px;"></span>'
        }), a("." + c + " .iconlist").append(f)
    }

    function f(b, c) {
        var d = a("#" + b).iconpicker("options"), e = a("#" + b).next("span").children(" .textbox-addon")[0].classList,
            f = e[e.length - 1];
        a("." + f + " .pre-icon") && a("." + f + " .pre-icon").remove(), a("." + f + " a").before('<a href="javascript:;" class="textbox-icon pre-icon ' + c + '" tabindex="-1" style="width: 26px; height: 28px;text-align: center;line-height:28px;color: #000"></a>'), 0 === a("." + f + " a.pre-icon .delBtn").length && d.delIcon && (a(".pre-icon").append('<a href="javascript:;" class="delBtn" style="display: inline-block;font-size: 14px;border: 1px solid #000;border-radius: 50%;width: 16px;height: 16px;line-height: 14px;vertical-align: text-bottom; margin-left: 5px;">x</a>'), a(".pre-icon").css("width", "45px"), a(".delBtn").click(function () {
            a("#" + b).iconpicker("clear"), a("." + f + " .pre-icon").remove()
        }))
    }

    a.fn.iconpicker = function (b, d) {
        if ("string" == typeof b) {
            var e = a.fn.iconpicker.methods[b];
            return e ? e(this, d) : this.combo(b, d)
        }
        this.each(function () {
            b = a.fn.iconpicker.parseOptions(this, b), a(this).combobox(b), c(this)
        })
    };
    var g = [];
    a.fn.iconpicker.methods = {}, a.fn.iconpicker.parseOptions = function (b, c) {
        var d = a.extend({}, a.fn.combobox.parseOptions(b), a.fn.iconpicker.defaults, a.parser.parseOptions(b, ["id"]), c);
        return setId(b, d)
    }, a.fn.iconpicker.defaults = {
        width: "100%", editable: !0, delIcon: !1, onShowPanel: function () {
            var b = (a(this).combobox("options"), a(this).combobox("panel")), c = b[0].children[0].className;
            1 != b[0].children.length && b[0].children[1].remove(), d(c)
        }, inputEvents: {keydown: b}, panelEvents: {
            mousedown: function (b) {
                var c = b.target.className, d = b.target.classList[0], e = b.handleObj.data.target.id;
                if (c && "fa" == d) {
                    var g = '<span class="fa ' + c + '" title="' + c + '"  ></span>';
                    a(".selectedIcon").html(g), a("#" + e).combobox("setValue", c).combobox("setText", c).combobox("hidePanel"), f(e, c)
                }
            }
        }
    }, a.parser && a.parser.plugins && a.parser.plugins.push("iconpicker");
})(jQuery);

function setId(a, b) {
    $(a);
    return void 0 == b.id && (b.id = getRandomNumByDef()), a.id = b.id, b
}

function getRandomNum(a, b) {
    switch (arguments.length) {
        case 1:
            return parseInt(Math.random() * a + 1);
        case 2:
            return parseInt(Math.random() * (b - a + 1) + a);
        default:
            return 0
    }
}

function getRandomNumByDef() {
    return getRandomNum(1e5, 999999)
}