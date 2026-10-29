import { defineStore } from 'pinia'
import { computed,ref } from 'vue'
import type {Journey} from '../types'

export const useJourneyStore=defineStore('journey',()=>{
  const journeys=ref<Journey[]>([]),selectedId=ref(''),loading=ref(false),error=ref(''),searched=ref(false)
  const selected=computed(()=>journeys.value.find(j=>j.id===selectedId.value)??journeys.value[0])
  async function plan(payload:object){
    loading.value=true;error.value='';searched.value=false
    try{
      const response=await fetch('/api/public/journeys/plan',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(payload),signal:AbortSignal.timeout(8000)})
      if(!response.ok)throw new Error(response.status>=500?'O planejador está temporariamente indisponível.':'Não foi possível validar os dados da busca.')
      const data=await response.json();journeys.value=data.journeys;selectedId.value=data.journeys[0]?.id??'';searched.value=true
    }catch(cause){
      journeys.value=[];selectedId.value='';searched.value=true
      error.value=cause instanceof Error&&cause.name==='TimeoutError'?'A consulta excedeu 8 segundos. Tente novamente.':cause instanceof Error?cause.message:'Não foi possível consultar o planejador.'
    }finally{loading.value=false}
  }
  return{journeys,selectedId,selected,loading,error,searched,plan}
})
