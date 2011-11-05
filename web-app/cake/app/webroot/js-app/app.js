/* Global configurations and options */
Ext.Loader.setConfig({enabled: true});
//Ext.Date.patterns = {
//    ISO8601Long:'Y-m-d H:i:s',
//    ISO8601Short:'Y-m-d',
//    ShortDate: 'n/j/Y',
//    LongDate: 'l, F d, Y',
//    FullDateTime: 'l, F d, Y g:i:s A',
//    MonthDay: 'F d',
//    ShortTime: 'g:i A',
//    LongTime: 'g:i:s A',
//    SortableDateTime: 'Y-m-d\\TH:i:s',
//    UniversalSortableDateTime: 'Y-m-d H:i:sO',
//    YearMonth: 'F, Y'
//};


Ext.application({
    name: 'SD',
    controllers: ['Surveys', 'Subjects', 'Users', 'Settings', 'Questions', 'Data'],
    autoCreateViewport : true,

    launch: function() {

    }
});