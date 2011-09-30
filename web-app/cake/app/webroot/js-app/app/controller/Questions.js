Ext.define('SD.controller.Questions', {
    extend: 'Ext.app.Controller',
    models: ['Question', 'Choice', 'Branch', 'Condition'],
    refs: [
        {ref: 'questionsList',      selector: 'surveysTab #questionsList' },
        {ref: 'questionDetails',    selector: 'surveysTab #questionDetails' },
        {ref: 'questionTypePanel',  selector: 'surveysTab #questionDetails #typePanel' },
//        {ref: 'scaleimagge',        selector: 'surveysTab #questionDetails #scaleimage' },
        {ref: 'choicesList',        selector: 'surveysTab #choicesList' },
        {ref: 'delChoiceBtn',       selector: 'surveysTab #choicesList button[action=delete]' },
        {ref: 'branchesList',       selector: 'surveysTab #branchesList' },
        {ref: 'delBranchBtn',       selector: 'surveysTab #branchesList button[action=delete]' },
        {ref: 'conditionsList',     selector: 'surveysTab #conditionsList' },
        {ref: 'delConditionBtn',    selector: 'surveysTab #conditionsList button[action=delete]' }
    ],
    init: function() {
        var me = this;
        me.control({
            'surveysTab #questionsList': {
                itemclick: me.onQuestionClick
            },
            'surveysTab #questionsList button[action=add]': {
                click: me.onAddQuestionBtnClick
            },
            'surveysTab #questionDetails button[action=save-question]': {
                click: me.onSaveQuestionBtnClick
            },
            'surveysTab #questionDetails button[action=delete-question]': {
                click: me.onDeleteQuestionBtnClick
            },
            '#questionDetails radiofield[name=q_type]': {
                change: me.onQuestionTypeChange
            },
            '#choicesList': {
                selectionchange: me.onChoicesListSelectionChange
            },
            '#choicesList button[action=add]': {
                click: me.addChoice
            },
            '#choicesList button[action=delete]': {
                click: me.deleteChoice
            },
            'surveysTab #branchesList': {
                itemclick: me.onBranchClick,
                selectionchange: me.onBranchesListSelectionChange
            },
            '#branchesList button[action=add]': {
                click: me.addBranch
            },
            '#branchesList button[action=delete]': {
                click: me.deleteBranch
            },
            '#conditionsList button[action=add]': {
                click: me.addCondition
            },
            '#conditionsList button[action=delete]': {
                click: me.deleteCondition
            }
        });
    },
    onLaunch: function() {
    },
    onQuestionClick: function(grid, question) {
        this.getChoicesList().bindStore(question.choices());
        this.getBranchesList().bindStore(question.branches());
        this.getQuestionDetails().loadRecord(question);
    },
    onBranchClick: function(grid, branch) {
        this.getConditionsList().bindStore(branch.conditions());
    },
    onAddQuestionBtnClick: function() {
        this.getQuestionsList().getStore().insert(0, Ext.create('SD.model.Question'));
        this.getQuestionsList().getPlugin().startEditByPosition({row: 0, column: 1});
    },
    onSaveQuestionBtnClick: function() {
        var form = this.getQuestionDetails().getForm(),
            question = form.getRecord();
        if (form.isValid()) {
            if (question)
                form.updateRecord(question);
            else
                this.getQuestionsList().getStore().add(form.getValues());
        }
    },
    onDeleteQuestionBtnClick: function() {
        var form = this.getQuestionDetails().getForm(),
            question = form.getRecord();
        if (question) {
            question.store.remove(question);
            this.onAddQuestionBtnClick();
            form.reset();
            form._record = undefined; // resetting the record, a bit of a hack!
        }
    },
    onQuestionTypeChange: function(field, newValue, oldValue, eOpts) {
        if (newValue){
            var typePanel = this.getQuestionTypePanel().getLayout();
            switch (field.inputValue) {
                case 0:
                case 1:
                    typePanel.setActiveItem('choicesList');
                    break;
                case 2:
                    typePanel.setActiveItem('scaletext');
                    break;
                case 3:
                    typePanel.setActiveItem('scaleimage');
                    break;
                case 4:
                    typePanel.setActiveItem('empty');
                    break;
            }
        }
    },
    addChoice: function() {
        this.getChoicesList().getStore().insert(0, Ext.create('SD.model.Choice'));
        this.getChoicesList().getPlugin().startEditByPosition({row: 0, column: 1});
    },
    deleteChoice: function() {
        var selection = this.getChoicesList().getSelectionModel().getSelection()[0];
        if (selection)
            this.getChoicesList().getStore().remove(selection);
    },
    onChoicesListSelectionChange: function(selModel, selections) {
        this.getDelChoiceBtn().setDisabled(selections.length === 0);
    },
    addBranch: function() {
        this.getBranchesList().getStore().insert(0, Ext.create('SD.model.Branch'));
        this.getBranchesList().getPlugin().startEditByPosition({row: 0, column: 0});
    },
    deleteBranch: function() {
        var selection = this.getBranchesList().getSelectionModel().getSelection()[0];
        if (selection)
            this.getBranchesList().getStore().remove(selection);
    },
    onBranchesListSelectionChange: function(selModel, selections) {
        this.getDelBranchBtn().setDisabled(selections.length === 0);
    },
    addCondition: function() {
        this.getConditionsList().getStore().insert(0, Ext.create('SD.model.Condition'));
        this.getConditionsList().getPlugin().startEdit(0, 1);
    },
    deleteCondition: function() {
        var selection = this.getConditionsList().getSelectionModel().getSelection()[0];
        if (selection)
            this.getConditionsList().getStore().remove(selection);
    }

});