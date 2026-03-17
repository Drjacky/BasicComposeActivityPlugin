package app.web.drjacky.basiccomposeactivityplugin

import com.android.tools.idea.wizard.template.Category
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.PackageNameWidget
import com.android.tools.idea.wizard.template.TemplateConstraint
import com.android.tools.idea.wizard.template.TemplateData
import com.android.tools.idea.wizard.template.WizardUiContext
import com.android.tools.idea.wizard.template.impl.defaultPackageNameParameter
import com.android.tools.idea.wizard.template.template
import java.io.File

val basicComposeActivityTemplate
    get() = template {
        name = "Basic Compose Activity"
        description = "Creates a multi-module Compose project with Navigation 3, Hilt, Material 3, detekt, and convention plugins"
        minApi = 24
        constraints = listOf(TemplateConstraint.AndroidX, TemplateConstraint.Kotlin)
        category = Category.Application
        formFactor = FormFactor.Mobile
        screens = listOf(WizardUiContext.NewProject)

        val packageName = defaultPackageNameParameter
        PendingTemplateConfig.store { packageName.value }

        widgets(
            PackageNameWidget(packageName),
        )

        thumb {
            File("compose-activity-material3")
                .resolve("template_compose_empty_activity_material3.png")
        }

        recipe = { _: TemplateData ->
        }
    }
