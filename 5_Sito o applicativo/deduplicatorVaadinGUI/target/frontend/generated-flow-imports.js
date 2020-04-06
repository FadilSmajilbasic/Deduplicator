const div = document.createElement('div');
div.innerHTML = '<custom-style><style include="lumo-color lumo-typography"></style></custom-style>';
document.head.insertBefore(div.firstElementChild, document.head.firstChild);

function addCssBlock(block) {
 const tpl = document.createElement('template');
 tpl.innerHTML = block;
 document.head.appendChild(tpl.content);
}
import $css_0 from 'Frontend/styles/login-form-style.css';
addCssBlock(`<custom-style><style>${$css_0}</style></custom-style>`);
import $css_1 from 'Frontend/styles/shared-styles.css';
addCssBlock(`<custom-style><style>${$css_1}</style></custom-style>`);
import $css_2 from 'Frontend/styles/vaadin-text-field-styles.css';
addCssBlock(`<dom-module id="flow_css_mod_2" theme-for="vaadin-text-field"><template><style>${$css_2}</style></template></dom-module>`);
import $css_3 from 'Frontend/styles/menu-buttons.css';
addCssBlock(`<custom-style><style>${$css_3}</style></custom-style>`);
import $css_4 from 'Frontend/styles/menu-links.css';
addCssBlock(`<custom-style><style>${$css_4}</style></custom-style>`);
import $css_5 from 'Frontend/styles/report-view.css';
addCssBlock(`<custom-style><style>${$css_5}</style></custom-style>`);
import $css_6 from 'Frontend/styles/scan-input.css';
addCssBlock(`<dom-module id="flow_css_mod_6" theme-for="vaadin-text-field"><template><style>${$css_6}</style></template></dom-module>`);
import $css_7 from 'Frontend/styles/radio-buttons-vertical.css';
addCssBlock(`<custom-style><style>${$css_7}</style></custom-style>`);

import '@vaadin/flow-frontend/contextMenuConnector-es6.js';
import '@vaadin/flow-frontend/datepickerConnector.js';
import '@vaadin/flow-frontend/dndConnector-es6.js';
import '@vaadin/flow-frontend/file-download-wrapper.js';
import '@vaadin/flow-frontend/flow-component-renderer.js';
import '@vaadin/flow-frontend/gridConnector-es6.js';
import '@vaadin/flow-frontend/timepickerConnector.js';
import '@vaadin/flow-frontend/vaadin-grid-flow-selection-column.js';
import '@vaadin/vaadin-app-layout/theme/lumo/vaadin-app-layout.js';
import '@vaadin/vaadin-app-layout/theme/lumo/vaadin-drawer-toggle.js';
import '@vaadin/vaadin-button/theme/lumo/vaadin-button.js';
import '@vaadin/vaadin-checkbox/theme/lumo/vaadin-checkbox.js';
import '@vaadin/vaadin-context-menu/theme/lumo/vaadin-context-menu.js';
import '@vaadin/vaadin-date-picker/theme/lumo/vaadin-date-picker.js';
import '@vaadin/vaadin-dialog/theme/lumo/vaadin-dialog.js';
import '@vaadin/vaadin-form-layout/theme/lumo/vaadin-form-item.js';
import '@vaadin/vaadin-form-layout/theme/lumo/vaadin-form-layout.js';
import '@vaadin/vaadin-grid/theme/lumo/vaadin-grid-column-group.js';
import '@vaadin/vaadin-grid/theme/lumo/vaadin-grid-column.js';
import '@vaadin/vaadin-grid/theme/lumo/vaadin-grid-sorter.js';
import '@vaadin/vaadin-grid/theme/lumo/vaadin-grid-tree-toggle.js';
import '@vaadin/vaadin-grid/theme/lumo/vaadin-grid.js';
import '@vaadin/vaadin-icons/vaadin-icons.js';
import '@vaadin/vaadin-lumo-styles/color.js';
import '@vaadin/vaadin-lumo-styles/icons.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/typography.js';
import '@vaadin/vaadin-notification/theme/lumo/vaadin-notification.js';
import '@vaadin/vaadin-ordered-layout/theme/lumo/vaadin-horizontal-layout.js';
import '@vaadin/vaadin-ordered-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/vaadin-progress-bar/theme/lumo/vaadin-progress-bar.js';
import '@vaadin/vaadin-radio-button/theme/lumo/vaadin-radio-button.js';
import '@vaadin/vaadin-radio-button/theme/lumo/vaadin-radio-group.js';
import '@vaadin/vaadin-select/theme/lumo/vaadin-select.js';
import '@vaadin/vaadin-text-field/theme/lumo/vaadin-number-field.js';
import '@vaadin/vaadin-text-field/theme/lumo/vaadin-password-field.js';
import '@vaadin/vaadin-text-field/theme/lumo/vaadin-text-field.js';
import '@vaadin/vaadin-time-picker/theme/lumo/vaadin-time-picker.js';
import '@vaadin/vaadin-upload/theme/lumo/vaadin-upload.js';
import '@vaadin/flow-frontend/contextMenuConnector.js';
import '@vaadin/flow-frontend/dndConnector.js';
import '@vaadin/flow-frontend/gridConnector.js';
import '@vaadin/flow-frontend/selectConnector.js';
var scripts = document.getElementsByTagName('script');
var thisScript;
var elements = document.getElementsByTagName('script');
for (var i = 0; i < elements.length; i++) {
    var script = elements[i];
    if (script.getAttribute('type')=='module' && script.getAttribute('data-app-id') && !script['vaadin-bundle']) {
        thisScript = script;break;
     }
}
if (!thisScript) {
    throw new Error('Could not find the bundle script to identify the application id');
}
thisScript['vaadin-bundle'] = true;
if (!window.Vaadin.Flow.fallbacks) { window.Vaadin.Flow.fallbacks={}; }
var fallbacks = window.Vaadin.Flow.fallbacks;
fallbacks[thisScript.getAttribute('data-app-id')] = {}
fallbacks[thisScript.getAttribute('data-app-id')].loadFallback = function loadFallback(){
   return import('./generated-flow-imports-fallback.js');
}