Ext.define("Soc.view.survey.Grid", {
    extend: "Ext.grid.Panel",
    alias: 'widget.surveysGrid',
    title: 'Surveys',
    store: 'Surveys',
    columns: [
        {
            text: 'Id',
            dataIndex: 'id'
        }, {
            text: 'Name',
            dataIndex: 'name'
        }, {
            text: 'Question Id',
            dataIndex: 'question_id'
        }, {
            text: 'Monday',
            dataIndex: 'mo'
        }, {
            text: 'Tuesday',
            dataIndex: 'tu'
        }, {
            text: 'Wednesday',
            dataIndex: 'we'
        }, {
            text: 'Thursday',
            dataIndex: 'th'
        }, {
            text: 'Friday',
            dataIndex: 'fr'
        }, {
            text: 'Saturday',
            dataIndex: 'sa'
        }, {
            text: 'Sunday',
            dataIndex: 'su'
        }, {
            text: 'Initializable by subject',
            dataIndex: 'subject_init'
        }, {
            text: 'Subject specific variables',
            dataIndex: 'subject_variables'
        }, {
            text: 'Date Created',
            dataIndex: 'created'
        }
    ]
});