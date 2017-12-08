import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { NotificationsService } from 'angular2-notifications';

import { PanelAdminManagementApi } from '../panel-admin-management-api.service';
import { PanelAdminApiKeysComponent } from './panel-admin-api-keys.component';

class NotificationsServiceMock {
}

class PanelAdminManagementApiMock {
	public getApiKeysPage() {}
	public blockApiKey() {}
	public unblockApiKey() {}
}

describe('PanelAdminApiKeysComponent', () => {
	let component: PanelAdminApiKeysComponent;
	let fixture: ComponentFixture<PanelAdminApiKeysComponent>;
	let notificationsServiceMock: NotificationsServiceMock;
	let panelAdminManagementApiMock: PanelAdminManagementApiMock;
	let panelAdminManagementApiMockGetApiKeysPageSpy: jasmine.Spy;

	const API_KEYS = {
		apiKeys: [
			{
				id: 10
			}
		],
		pager: {
			pageNumber: 0,
			pageSize: 20
		}
	};
	const ACCOUNT_ID = 10;
	const API_KEY_ID = 15;
	const BLOCK_RESULT = {
		successful: true
	};
	const UNBLOCK_RESULT = {
		successful: true
	};

	beforeEach(async(() => {
		notificationsServiceMock = new NotificationsServiceMock();
		panelAdminManagementApiMock = new PanelAdminManagementApiMock();

		panelAdminManagementApiMockGetApiKeysPageSpy = spyOn(panelAdminManagementApiMock, 'getApiKeysPage').and.returnValue(Promise.resolve(API_KEYS));
		spyOn(panelAdminManagementApiMock, 'blockApiKey').and.returnValue(Promise.resolve(BLOCK_RESULT));
		spyOn(panelAdminManagementApiMock, 'unblockApiKey').and.returnValue(Promise.resolve(UNBLOCK_RESULT));

		TestBed.configureTestingModule({
			schemas: [NO_ERRORS_SCHEMA],
			declarations: [PanelAdminApiKeysComponent],
			providers: [
				{
					provide: NotificationsService,
					useValue: notificationsServiceMock
				},
				{
					provide: PanelAdminManagementApi,
					useValue: panelAdminManagementApiMock
				}
			]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(PanelAdminApiKeysComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should load api keys', () => {
		expect(component.hasApiKeys()).toBeFalse();
		expect(component.getApiKeys()).toEqual([]);

		fixture.whenStable().then(() => {
			expect(component.getApiKeys()).toEqual(API_KEYS.apiKeys);
			expect(component.hasApiKeys()).toBeTrue();
		});
	});

	it('should block api key', () => {
		fixture.whenStable().then(() => {
			expect(panelAdminManagementApiMockGetApiKeysPageSpy.calls.count()).toBe(1);

			component.blockApiKey(ACCOUNT_ID, API_KEY_ID);

			fixture.whenStable().then(() => {
				expect(panelAdminManagementApiMock.blockApiKey).toHaveBeenCalledWith({
					accountId: ACCOUNT_ID,
					apiKeyId: API_KEY_ID
				});

				expect(panelAdminManagementApiMockGetApiKeysPageSpy.calls.count()).toBe(2);
			});
		});
	});
});
