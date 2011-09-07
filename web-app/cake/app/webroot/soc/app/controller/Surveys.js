Ext.define('Soc.controller.Surveys', {
    extend: 'Ext.app.Controller',

    models: ['Survey'],

    stores: ['Surveys', 'Subjects', 'Users'],

    init: function() {
        var me = this;
        console.log('Surveys controller initializing');
    },
    onLaunch: function() {
    }
});