import { test, expect } from '@playwright/test';

test('test', async ({ page }) => {
  await page.goto('http://localhost:5173/login');
  await page.getByRole('main').click();
  await page.getByRole('main').click();
  await page.getByRole('link', { name: 'Register User Account' }).click();
  await page.getByRole('textbox', { name: 'USERNAME' }).click();
  await page.getByRole('textbox', { name: 'USERNAME' }).fill('Andrei');
  await page.getByRole('textbox', { name: 'EMAIL' }).click();
  await page.getByRole('textbox', { name: 'EMAIL' }).fill('mariustrif0323@yahoo.com');
  await page.getByRole('textbox', { name: 'PASSWORD', exact: true }).click();
  await page.getByRole('textbox', { name: 'PASSWORD', exact: true }).fill('ParolaMarius');
  await page.getByRole('textbox', { name: 'CONFIRM PASSWORD' }).click();
  await page.getByRole('textbox', { name: 'CONFIRM PASSWORD' }).fill('ParolaMarius');
  await page.getByRole('button', { name: 'REGISTER ACCOUNT' }).click();
  await page.getByRole('textbox', { name: 'EMAIL' }).click();
  await page.getByRole('textbox', { name: 'PASSWORD' }).click();
  await page.getByRole('textbox', { name: 'PASSWORD' }).fill('ParolaMarius');
  await page.getByRole('textbox', { name: 'EMAIL' }).click();
  await page.getByRole('textbox', { name: 'EMAIL' }).fill('mariustrif0323@yahoo.com');
  await page.getByRole('button', { name: 'INITIALIZE SESSION' }).click();
  await page.getByRole('button', { name: 'Upvote Authentication token expires during active sessions' }).click();

});