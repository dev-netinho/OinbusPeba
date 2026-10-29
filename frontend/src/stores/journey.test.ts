import {beforeEach,describe,expect,it,vi} from 'vitest'
import {createPinia,setActivePinia} from 'pinia'
import {useJourneyStore} from './journey'
describe('journey store',()=>{beforeEach(()=>setActivePinia(createPinia()));it('shows failure without replacing it with successful local data',async()=>{vi.stubGlobal('fetch',vi.fn().mockRejectedValue(new Error('Backend indisponível')));const store=useJourneyStore();await store.plan({});expect(store.error).toContain('Backend indisponível');expect(store.journeys).toEqual([]);expect(store.searched).toBe(true)})})
