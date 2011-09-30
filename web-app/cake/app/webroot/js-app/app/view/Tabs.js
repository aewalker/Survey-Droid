Ext.define("SD.view.Tabs", {
    extend: "Ext.tab.Panel",
    alias: 'widget.mainTabs',
    requires: ['SD.view.subject.Grid', 'SD.view.survey.Tab', 'SD.view.user.Grid', 'SD.view.configuration.Tab'],
    items: [
        { xtype: 'surveysTab' },
        { xtype: 'subjectsGrid' },
        {
            title: 'Data',
            html: 'Data will be displayed here'
        }, {
            xtype: 'usersGrid'
        }, {
            itemId: 'settingsTab',
            title: 'Settings',
            layout: 'fit'
        }

    ]
});