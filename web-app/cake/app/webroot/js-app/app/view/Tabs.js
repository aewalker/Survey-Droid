Ext.define("SD.view.Tabs", {
    extend: "Ext.tab.Panel",
    alias: 'widget.mainTabs',
    requires: ['SD.view.subject.Grid', 'SD.view.survey.Tab', 'SD.view.user.Grid', 'SD.view.configuration.Tab', 'SD.view.data.Tab'],
    items: [
        {
            xtype: 'usersGrid'
        }, {
            xtype: 'subjectsGrid'
        }, {
            xtype: 'surveysTab'
        }, {
            itemId: 'dataTab',
            xtype: 'dataTab'
        }, {
            itemId: 'settingsTab',
            title: 'Settings',
            xtype: 'configurationsTab'
        }

    ]
});