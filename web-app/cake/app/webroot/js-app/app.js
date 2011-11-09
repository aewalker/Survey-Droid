/* Global configurations and options */
Ext.Loader.setConfig({enabled: true});

Ext.application({
    name: 'SD',
    controllers: ['Surveys', 'Subjects', 'Users', 'Settings', 'Questions', 'Data'],
    autoCreateViewport : true,

    launch: function() {

    }
});