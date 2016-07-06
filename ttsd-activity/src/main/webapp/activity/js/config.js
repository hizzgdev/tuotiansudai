var require = {
    'paths': {
        'text': 'activity/js/libs/text-2.0.14',
        'jquery': 'activity/js/libs/jquery-1.11.3.min',
        'csrf': 'activity/js/libs/csrf',
        'jqueryPage': 'activity/js/libs/jquery.page',
        'jquery.validate': 'activity/js/libs/jquery.validate-1.14.0.min',
        'jquery.form': 'activity/js/libs/jquery.form-3.51.0.min',
        'autoNumeric': 'activity/js/libs/autoNumeric',
        'mustache': 'activity/js/libs/mustache-2.1.3.min',
        'moment': 'activity/js/libs/moment-2.10.6.min',
        'underscore': 'activity/js/libs/underscore-1.8.3.min',
        'jquery.ajax.extension': 'activity/js/jquery_ajax_extension',
        'daterangepicker': 'activity/js/libs/jquery.daterangepicker-0.0.7',
        'pagination': 'activity/js/pagination',
        'lodash': 'activity/js/libs/lodash.min',
        'layer1': 'activity/js/libs/layer/layer',
        'layer-extend': 'activity/js/libs/layer/extend/layer.ext',
        'echarts': 'activity/js/libs/echarts/dist',
        'jquery.validate.extension': 'activity/js/jquery_validate_extension',
        'commonFun': 'activity/js/common',
        'layerWrapper': 'activity/js/wrapper-layer',
        'fullPage':'activity/js/libs/jquery.fullPage.min',
        'swiper':'activity/js/libs/swiper-3.2.7.jquery.min',
        'load-swiper':'activity/js/load_swiper',
        'coupon-alert': 'activity/js/coupon_alert',
        'cnzz-statistics': 'activity/js/cnzz_statistics',
        'red-envelope-float': 'activity/js/red-envelope-float',
        'drag': 'activity/js/libs/drag',
        'rotate': 'activity/js/libs/jqueryrotate.min',
        'template':'activity/js/libs/template.min',
        'fancybox':'activity/js/libs/jquery.fancybox.min',
        'count_down': 'activity/js/count_down',
        'placeholder':'activity/js/libs/jquery.enplaceholder',
        'superslide': 'activity/js/libs/jquery.SuperSlide.2.1.1'
    },
    'waitSeconds':0,
    'shim': {
        'jquery.validate': ['jquery'],
        'jquery.form': ['jquery'],
        'jqueryPage': ['jquery'],
        'autoNumeric': ['jquery'],
        'pagination': ['jquery'],
        'layer1': ['jquery'],
        'layer-extend': ['jquery','layer1'],
        'layerWrapper':['layer1','layer-extend'],
        'commonFun': ['jquery.validate', 'echarts/echarts.min'],
        'jquery.validate.extension': ['jquery', 'jquery.validate'],
        'fullPage': ['jquery'],
        'swiper':['jquery'],
        'load-swiper':['swiper'],
        'drag':['jquery'],
        'rotate':['jquery'],
        'fancybox':['jquery'],
        'placeholder': ['jquery'],
        'superslide': ['jquery']
    },
    config: {
        text: {
            useXhr: function (url, protocol, hostname, port) {
                return true;
            }
        }
    }
};

if (this.document) {
    require.baseUrl = staticServer;
}
