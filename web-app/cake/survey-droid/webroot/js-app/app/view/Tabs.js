Ext.define("SD.view.Tabs", {
    extend: "Ext.tab.Panel",
    alias: 'widget.mainTabs',
    requires: ['SD.view.subject.Tab', 'SD.view.survey.Tab', 'SD.view.user.Grid', 'SD.view.setting.Tab', 'SD.view.data.Tab'],
    items: [
        {
            xtype: 'usersGrid'
        }, {
            itemId: 'subjectsTab',
            xtype: 'subjectsTab'
        }, {
            xtype: 'surveysTab'
        }, {
            itemId: 'dataTab',
            xtype: 'dataTab'
        }, {
            itemId: 'settingsTab',
            title: 'Settings',
            xtype: 'settingsTab'
        }
    ]
});