Ext.define('SD.controller.Questions', {
    extend: 'Ext.app.Controller',
    models: ['Question', 'Choice', 'Branch', 'Condition'],
    refs: [
        {ref: 'questionsList',      selector: 'surveysTab #questionsList' },
        {ref: 'questionDetails', selector: 'surveysTab #questionDetails' },
        {ref: 'choicesList',        selector: 'surveysTab #choicesList' },
        {ref: 'branchesList',       selector: 'surveysTab #branchesList' },
        {ref: 'conditionsList',     selector: 'surveysTab #conditionsList' }
    ],
    init: function() {
        var me = this;
        me.control({
            'surveysTab #questionsList': {
                itemclick: me.onQuestionClick
            },
            'surveysTab #questionDetails button[action=save]': {
                click: me.onQuestionsDetailSave
            }
        });
    },
    onLaunch: function() {
    },
    onQuestionClick: function(grid, question) {
        this.getChoicesList().bindStore(question.choices());
        this.getBranchesList().bindStore(question.branches());
    },
    onQuestionsDetailSave: function() {
        var form = this.getQuestionDetails().getForm();
        console.log(form.getRecord());
        console.log(form.isValid());
        if (form.isValid())
            form.updateRecord(form.getRecord());
    }
});