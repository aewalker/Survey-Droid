Ext.define('SD.controller.Configurations', {
    extend: 'Ext.app.Controller',
    models: ['Configuration'],
    stores: ['Configurations'],

    init: function() {
        console.log('configurations initting');
    },
    onLaunch: function() {
    }
});