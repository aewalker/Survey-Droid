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
        {ref: 'delConditionBtn',    selector: 'surveysTab #conditionsList button[action=delete]' },
        {ref: 'lowImage', selector: '#img_low' }
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
//        this.getLowImage().setSrc('data:image/png;base64,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');
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