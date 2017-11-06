import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AgGridModule } from 'ag-grid-angular/main';
import * as hljs from 'highlight.js';
import { HighlightJsModule, HIGHLIGHT_JS } from 'angular-highlight-js';
import { CookieService } from 'ngx-cookie-service';

export function highlightJsFactory() {
  return hljs;
}

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { AboutComponent } from './about/about.component';
import { ApiBrowserComponent } from './api-browser/api-browser.component';
import { ApiDocumentationComponent } from './api-documentation/api-documentation.component';
import { LicensingComponent } from './licensing/licensing.component';
import { StatisticsComponent } from './statistics/statistics.component';
import { EntityStatisticsCloudComponent } from './statistics/entity-statistics-cloud.component';
import { EntityHitsGridComponent } from './statistics/entity-hits-grid.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

import { RestApiService } from './rest-api/rest-api.service';
import { RestClientFactoryService } from './rest-api/rest-client-factory.service';
import { WindowReferenceService } from './window-reference/window-reference.service';
import { InfoComponent } from './info/info.component';
import { PanelComponent } from './panel/panel.component';
import { PanelApiKeysComponent } from './panel/api-keys/panel-api-keys.component';
import { PanelAccountSettingsComponent } from './panel/account-settings/panel-account-settings.component';
import { PanelAdminManagementComponent } from './panel/admin-management/panel-admin-management.component';

const appRoutes: Routes = [
	{
		path: '',
		component: HomeComponent
	},
	{
		path: 'about',
		component: AboutComponent
	},
	{
		path: 'api-browser',
		component: ApiBrowserComponent
	},
	{
		path: 'api-documentation',
		component: ApiDocumentationComponent
	},
	{
		path: 'licensing',
		component: LicensingComponent
	},
	{
		path: 'statistics',
		component: StatisticsComponent
	},
	{
		path: 'panel',
		component: PanelComponent
	},
	{
		path: '**',
		component: PageNotFoundComponent
	}
];

export function initConfiguration(restApiService: RestApiService): Function {
	return () => restApiService.init();
}

@NgModule({
	declarations: [
		AppComponent,
		HomeComponent,
		AboutComponent,
		ApiBrowserComponent,
		ApiDocumentationComponent,
		LicensingComponent,
		StatisticsComponent,
		EntityStatisticsCloudComponent,
		EntityHitsGridComponent,
		PageNotFoundComponent,
		InfoComponent,
		PanelComponent,
		PanelApiKeysComponent,
		PanelAccountSettingsComponent,
		PanelAdminManagementComponent
	],
	imports: [
		RouterModule.forRoot(appRoutes),
		BrowserModule,
		FormsModule,
		ReactiveFormsModule,
		AgGridModule.withComponents([]),
		HighlightJsModule.forRoot({
			provide: HIGHLIGHT_JS,
			useFactory: highlightJsFactory
		})
	],
	providers: [
		{
			provide: APP_INITIALIZER,
			useFactory: initConfiguration,
			multi: true,
			deps: [RestApiService]
		},
		RestApiService,
		RestClientFactoryService,
		WindowReferenceService,
		CookieService
	],
	bootstrap: [AppComponent]
})
export class AppModule { }