/**
 * [name]:share android page
 * [user]:xuqiang
 * [date]:2016-07-29
 */
require(['jquery', 'layerWrapper', 'underscore', 'jquery.validate', 'jquery.validate.extension', 'jquery.ajax.extension'], function($, layer, _) {
	$(function() {

		var $androidForm = $('#androidForm'),
			$iosForm = $('#iosForm'),
			$androidBtn = $('#androidBtn'),
			$iosBtn = $('#iosBtn'),
			countdown = 60,
			sendSms = {
				androidCount: function() { //安卓倒计时
					timer = setInterval(function() {
						$androidBtn.prop('disabled', true).val(countdown + 's');
						countdown--;
						if (countdown == 0) {
							clearInterval(timer);
							countdown = 60;
							$androidBtn.prop('disabled', false).val('重新发送');
						}
					}, 1000);
				},
				anCaptcha: function(state) { //安卓-获取手机验证码
					if (state == false) {
						$.ajax({
								url: '/register/user/'+ $('#mobile').val() +'/send-register-captcha',
								type: 'get',
								dataType: 'json'

							})
							.done(function(data) {
								if (data.data.status && !data.data.isRestricted) {
									sendSms.androidCount();
								} else if (!data.data.status && data.data.isRestricted) {
									layer.msg('短信发送频繁,请稍后再试');
								}
							})
							.fail(function() {
								layer.msg('请求失败，请重试！');
							});
					}else {
						var param = JSON.parse('{"' + decodeURI(location.search.substring(1)).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
						location.href = '/activity/app-share?referrerMobile=' + param["referrerMobile"];
					}
				},
				iosCount: function() { //ios倒计时
					timer = setInterval(function() {
						$iosBtn.prop('disabled', true).val(countdown + 's');
						countdown--;
						if (countdown == 0) {
							clearInterval(timer);
							countdown = 60;
							$iosBtn.prop('disabled', false).val('重新发送');
						}
					}, 1000);
				},
				iosCaptcha: function(state) { //ios-获取手机验证码
					if (state == false) {
						$.ajax({
							url: '/register/user/' + $('#mobile').val() + '/send-register-captcha',
							type: 'get',
							dataType: 'json'
						})
							.done(function (data) {
								if (data.data.status && !data.data.isRestricted) {
									sendSms.iosCount();
								} else if (!data.data.status && data.data.isRestricted) {
									layer.msg('短信发送频繁,请稍后再试');
								}
							})
							.fail(function () {
								layer.msg('请求失败，请重试！');
							});
					}else {
						var param = JSON.parse('{"' + decodeURI(location.search.substring(1)).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')
						location.href = '/activity/app-share?referrerMobile=' + param["referrerMobile"];
					}
				}
			};


		// isphone validate
		jQuery.validator.addMethod("isPhone", function(value, element) {
			var tel = /0?(13|14|15|18|17)[0-9]{9}/;
			return this.optional(element) || (tel.test(value));
		}, "请正确填写您的手机号码");

		//android validate && submit data
		$androidForm.validate({
			debug: true,
			focusInvalid: false,
			ignore: '.ignore',
			rules: {
				mobile: {
					required: true,
					digits: true,
					isPhone: true,
					minlength: 11,
					maxlength: 11,
					isExist: '/register/user/mobile/{0}/is-exist'
				},
				password: {
					required: true,
					regex: /^(?=.*[^\d])(.{6,20})$/
				},
				captcha: {
					required: true,
					digits: true,
					maxlength: 6,
					minlength: 6,
					captchaVerify: {
						param: function() {
							var mobile = $('#mobile').val();
							return "/register/user/mobile/" + mobile + "/captcha/{0}/verify"
						}
					}
				},
				agree: {
					required: true
				}
			},
			messages: {
				mobile: {
					required: '请输入手机号',
					digits: '必须是数字',
					minlength: '手机格式不正确',
					isPhone: '请输入正确的手机号码',
					maxlength: '手机格式不正确',
					isExist: '手机号已存在'
				},
				password: {
					required: "请输入密码",
					regex: '6位至20位，不能全是数字'
				},
				captcha: {
					required: '请输入手机验证码',
					digits: '验证码格式不正确',
					maxlength: '验证码格式不正确',
					minlength: '验证码格式不正确',
					captchaVerify: '验证码不正确'
				},
				agree: '请同意服务协议'
			},
			errorPlacement: function(error, element) {
				error.appendTo(element.parent());
			},
			submitHandler: function(form) {
				$.ajax({
					url: '/register/user/shared',
					type: 'POST',
					dataType: 'json',
					data: {
						mobile: $('#mobile').val(),
						password: $('#password').val(),
						captcha: $('#captcha').val(),
						referrer: JSON.parse('{"' + decodeURI(location.search.substring(1)).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')["referrerMobile"],
						agreement:$('#agreement').attr("checked")=="checked"
					}
				})
				.done(function(data) {
						if (data.data.status == true) {
							location.href = '/activity/app-share?referrerMobile=' + location.href.split('referrerMobile=')[1];
						} else {
							layer.msg('请求失败，请重试！');
					}
				})
				.fail(function() {
					layer.msg('请求失败，请重试！');
				});
				
			}
		});

		//ios validate && submit data
		$iosForm.validate({
			debug: true,
			focusInvalid: false,
			ignore: '.ignore',
			rules: {
				mobile: {
					required: true,
					digits: true,
					isPhone: true,
					minlength: 11,
					maxlength: 11,
					isExist: '/register/user/mobile/{0}/is-exist'
				},
				captcha: {
					required: true,
					digits: true,
					maxlength: 6,
					minlength: 6,
					captchaVerify: {
						param: function() {
							var mobile = $('#mobile').val();
							return "/register/user/mobile/" + mobile + "/captcha/{0}/verify"
						}
					}
				},
				agree: {
					required: true
				}
			},
			messages: {
				mobile: {
					required: '请输入手机号',
					digits: '必须是数字',
					minlength: '手机格式不正确',
					isPhone: '请输入正确的手机号码',
					maxlength: '手机格式不正确',
					isExist: '手机号已存在'
				},
				captcha: {
					required: '请输入手机验证码',
					digits: '验证码格式不正确',
					maxlength: '验证码格式不正确',
					minlength: '验证码格式不正确',
					captchaVerify: '验证码不正确'
				},
				agree: '请同意服务协议'
			},
			errorPlacement: function(error, element) {
				error.appendTo(element.parent());
			},
			submitHandler: function(form) {
				$.ajax({
					url: '/register/user/shared-prepare',
					type: 'POST',
					dataType: 'json',
					data: {
						mobile: $('#mobile').val(),
						captcha: $('#captcha').val(),
						referrerMobile: JSON.parse('{"' + decodeURI(location.search.substring(1)).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}')["referrerMobile"]
					}
				})
					.done(function (data) {
						if (data.data.status == true) {
							location.href = '/activity/app-share?referrerMobile=' + location.href.split('referrerMobile=')[1];
						} else {
						layer.msg('请求失败，请重试！');
						}
					})
					.fail(function () {
						layer.msg('请求失败，请重试！');
					});
			}
		});

		//get code event
		$androidBtn.on('click', function(event) {
			event.preventDefault();
			$.ajax({
				url: '/register/user/mobile/' + $('#mobile').val() + '/is-register', //获取手机验证码接口
				type: 'get',
				dataType: 'json'
			})
			.done(function(data) {
				sendSms.anCaptcha(data.data.status);
			})
			.fail(function(data) {
				layer.msg('请求失败，请重试');
			});
		});

		//get code event
		$iosBtn.on('click', function(event) {
			event.preventDefault();
			$.ajax({
				url: '/register/user/mobile/' + $('#mobile').val() + '/is-register', //判断手机号是否存在
				type: 'get',
				dataType: 'json'
			})
			.done(function(data) {
				sendSms.iosCaptcha(data.data.status);
			})
			.fail(function(data) {
				layer.msg('请求失败，请重试');
			});
		});
	});
});