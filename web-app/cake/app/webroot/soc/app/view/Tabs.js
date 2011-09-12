Ext.define("Soc.view.Tabs", {
    extend: "Ext.tab.Panel",
    alias: 'widget.mainTabs',
    requires: ['Soc.view.subject.Grid', 'Soc.view.survey.Grid', 'Soc.view.user.Grid'],
    items: [
        { xtype: 'subjectsGrid' },
        { xtype: 'surveysGrid' },
        { title: 'Data' },
        {
            xtype: 'usersGrid'
        }, {
            title: 'Settings'
        }

    ]
});