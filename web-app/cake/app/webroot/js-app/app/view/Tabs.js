Ext.define("SD.view.Tabs", {
    extend: "Ext.tab.Panel",
    alias: 'widget.mainTabs',
    requires: ['SD.view.subject.Grid', 'SD.view.survey.Tab', 'SD.view.user.Grid'],
    items: [
        { xtype: 'surveysTab' },
        { xtype: 'subjectsGrid' },

        { title: 'Data' },
        {
            xtype: 'usersGrid'
        }, {
            title: 'Settings'
        }

    ]
});