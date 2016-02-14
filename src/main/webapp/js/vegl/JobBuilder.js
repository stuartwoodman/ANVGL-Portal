Ext.application({
    /** 
     * @lends JobBuilder 
     */ 
    name : 'anvgl',
    
    /**
     * Creates the wizard towards a job submission: <br/>
     * - Upload input files <br/>
     * - Select solution <br/>
     * - Select storage and resources <br/>
     * - Review and submit
     * @constructs
     */
    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout: 'border',
            style: {
                'background-color': 'white'
            },
            items: [{
                xtype: 'box',
                region: 'north',
                applyTo: 'body',
                height: 100
            }, {
                region: 'center',
                margin: '10 0 10 0',
                border: false,
                layout: 'center',
                bodyStyle: {
                    'background-color': 'white'
                },
                items: [{
                    id: 'job-submit-panel',
                    maxWidth: 1000,
                    width: '100%',
                    height: '100%',
                    bodyStyle: {
                        'background-color': 'white'
                    },
                    layout: 'fit',
                    items: [ Ext.create('vegl.jobwizard.JobWizard', {
                        id : 'job-wizard-panel',
                        forms : [
                                 'vegl.jobwizard.forms.JobSeriesForm',
                                 'vegl.jobwizard.forms.JobUploadForm',
                                 'vegl.jobwizard.forms.ScriptBuilderForm',
                                 'vegl.jobwizard.forms.JobObjectForm',
                                 'vegl.jobwizard.forms.JobSubmitForm'
                             ]
                    }) ]
                }]
            }]
        });
    }
});