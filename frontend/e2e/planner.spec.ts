import {test,expect} from '@playwright/test'
test('catálogo indisponível não é substituído por dados locais',async({page})=>{
  await page.goto('/');
  await expect(page.getByText('Projeto acadêmico — veículos e estimativas simulados.')).toBeVisible();
  await expect(page.getByRole('heading',{name:'Planeje seu caminho'})).toBeVisible();
  await expect(page.getByText('Catálogo indisponível. Nenhuma lista substituta foi usada.').first()).toBeVisible({timeout:12_000});
  await expect(page.getByRole('heading',{name:'Detalhes do trajeto'})).toHaveCount(0);
  await expect(page.getByRole('heading',{name:'Entrar no OinbuPebas'})).toBeVisible();
})
