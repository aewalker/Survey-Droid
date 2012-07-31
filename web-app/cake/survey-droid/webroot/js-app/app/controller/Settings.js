Ext.define('SD.controller.Settings', {
    extend: 'Ext.app.Controller',
    models: ['Configuration'],
    stores: ['Configurations'],
    refs: [
        {ref: 'mainTabs', selector: 'mainTabs' },
        {ref: 'settingsTab', selector: '#settingsTab' },
        {ref: 'settingsForm', selector: '#settingsForm' }
    ],
    init: function() {
        var me = this;
        me.control({
            '#settingsTab': {
                activate: me.refreshSettingsForm
            },
            '#settingsForm button[action=save]': {
                click: me.saveSettingsForm
            }
        })
        me.getConfigurationsStore().on('load', me.refreshSettingsForm, me);
    },
    refreshSettingsForm: function() {
        var me = this,
            form = me.getSettingsForm().getForm(),
            records = me.getConfigurationsStore().getRange();
        Ext.each(records, function(item, index) {
            var tmp = {};
            tmp[item.data.c_key] = item.data.c_value;
            form.setValues(tmp);
        })
    },
    saveSettingsForm: function() {
        var me = this,
            store = me.getConfigurationsStore(),
            values = me.getSettingsForm().getForm().getValues();
        Ext.each(store.getRange(), function(item, index) {
            item.set('c_value', values[item.data.c_key]);
        })
        store.sync();
    },
    onLaunch: function() {
        //this.getMainTabs().setActiveTab('settingsTab');
    }
});