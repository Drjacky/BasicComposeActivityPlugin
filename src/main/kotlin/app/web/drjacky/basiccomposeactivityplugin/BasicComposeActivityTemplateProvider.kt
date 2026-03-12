package app.web.drjacky.basiccomposeactivityplugin

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider

class BasicComposeActivityTemplateProvider : WizardTemplateProvider() {
    override fun getTemplates(): List<Template> = listOf(basicComposeActivityTemplate)
}
