Ext.define('Soc.model.User', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',         type: 'int'},
        {name: 'username',   type: 'string'},
        {name: 'email',      type: 'string'},
        {name: 'password',   type: 'string'},
        {name: 'first_name', type: 'string'},
        {name: 'last_name',  type: 'string'},
        {name: 'admin',      type: 'boolean'}
    ],
    proxy: {
        type: 'rest',
        url : '/rest/users',
        reader: {
            type: 'json',
            record: 'User'
        }
    }
});